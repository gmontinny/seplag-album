terraform {
  required_version = ">= 1.5"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 6.0"
    }
  }

  backend "gcs" {
    bucket = "seplag-album-tfstate"
    prefix = "gcp/terraform.tfstate"
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

# ─── VPC ────────────────────────────────────────────────────────────────────────

resource "google_compute_network" "main" {
  name                    = "${var.app_name}-vpc"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "gke" {
  name          = "${var.app_name}-gke-subnet"
  ip_cidr_range = "10.0.0.0/20"
  region        = var.region
  network       = google_compute_network.main.id

  secondary_ip_range {
    range_name    = "pods"
    ip_cidr_range = "10.4.0.0/14"
  }

  secondary_ip_range {
    range_name    = "services"
    ip_cidr_range = "10.8.0.0/20"
  }

  private_ip_google_access = true
}

resource "google_compute_global_address" "private_ip" {
  name          = "${var.app_name}-private-ip"
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = 16
  network       = google_compute_network.main.id
}

resource "google_service_networking_connection" "private" {
  network                 = google_compute_network.main.id
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.private_ip.name]
}

resource "google_compute_router" "main" {
  name    = "${var.app_name}-router"
  region  = var.region
  network = google_compute_network.main.id
}

resource "google_compute_router_nat" "main" {
  name                               = "${var.app_name}-nat"
  router                             = google_compute_router.main.name
  region                             = var.region
  nat_ip_allocate_option             = "AUTO_ONLY"
  source_subnetwork_ip_ranges_to_nat = "ALL_SUBNETWORKS_ALL_IP_RANGES"
}

# ─── GKE ────────────────────────────────────────────────────────────────────────

resource "google_container_cluster" "main" {
  name     = "${var.app_name}-${var.environment}"
  location = var.region

  network    = google_compute_network.main.id
  subnetwork = google_compute_subnetwork.gke.id

  initial_node_count       = 1
  remove_default_node_pool = true

  ip_allocation_policy {
    cluster_secondary_range_name  = "pods"
    services_secondary_range_name = "services"
  }

  private_cluster_config {
    enable_private_nodes    = true
    enable_private_endpoint = false
    master_ipv4_cidr_block  = "172.16.0.0/28"
  }

  workload_identity_config {
    workload_pool = "${var.project_id}.svc.id.goog"
  }

  release_channel {
    channel = "REGULAR"
  }

  deletion_protection = var.environment == "prod"
}

resource "google_container_node_pool" "default" {
  name     = "default-pool"
  location = var.region
  cluster  = google_container_cluster.main.name

  autoscaling {
    min_node_count = var.gke_min_nodes
    max_node_count = var.gke_max_nodes
  }

  node_config {
    machine_type    = var.gke_machine_type
    service_account = google_service_account.gke_nodes.email
    oauth_scopes    = ["https://www.googleapis.com/auth/cloud-platform"]

    workload_metadata_config {
      mode = "GKE_METADATA"
    }
  }
}

# ─── Cloud SQL PostgreSQL ───────────────────────────────────────────────────────

resource "google_sql_database_instance" "main" {
  name             = "${var.app_name}-${var.environment}"
  database_version = "POSTGRES_16"
  region           = var.region

  depends_on = [google_service_networking_connection.private]

  settings {
    tier              = var.cloudsql_tier
    availability_type = var.environment == "prod" ? "REGIONAL" : "ZONAL"
    disk_size         = 20
    disk_autoresize   = true
    disk_type         = "PD_SSD"

    ip_configuration {
      ipv4_enabled                                  = false
      private_network                               = google_compute_network.main.id
      enable_private_path_for_google_cloud_services = true
    }

    backup_configuration {
      enabled                        = true
      point_in_time_recovery_enabled = var.environment == "prod"
      start_time                     = "03:00"
      backup_retention_settings {
        retained_backups = var.environment == "prod" ? 7 : 1
      }
    }

    insights_config {
      query_insights_enabled = var.environment == "prod"
    }
  }

  deletion_protection = var.environment == "prod"
}

resource "google_sql_database" "main" {
  name     = "seplag_album"
  instance = google_sql_database_instance.main.name
}

resource "google_sql_user" "main" {
  name     = var.db_username
  instance = google_sql_database_instance.main.name
  password = var.db_password
}

# ─── Cloud Storage (substitui MinIO) ───────────────────────────────────────────

resource "google_storage_bucket" "albuns" {
  name          = "${var.project_id}-albuns-${var.environment}"
  location      = var.region
  storage_class = "STANDARD"
  force_destroy = var.environment != "prod"

  uniform_bucket_level_access = true

  versioning {
    enabled = true
  }

  lifecycle_rule {
    condition {
      age = 90
    }
    action {
      type          = "SetStorageClass"
      storage_class = "NEARLINE"
    }
  }

  public_access_prevention = "enforced"
}

# ─── Artifact Registry ─────────────────────────────────────────────────────────

resource "google_artifact_registry_repository" "main" {
  location      = var.region
  repository_id = var.app_name
  format        = "DOCKER"

  cleanup_policies {
    id     = "keep-last-20"
    action = "KEEP"
    most_recent_versions {
      keep_count = 20
    }
  }
}

# ─── IAM (Workload Identity) ───────────────────────────────────────────────────

resource "google_service_account" "gke_nodes" {
  account_id   = "${var.app_name}-gke-nodes"
  display_name = "GKE Nodes SA"
}

resource "google_project_iam_member" "gke_nodes_log" {
  project = var.project_id
  role    = "roles/logging.logWriter"
  member  = "serviceAccount:${google_service_account.gke_nodes.email}"
}

resource "google_project_iam_member" "gke_nodes_metrics" {
  project = var.project_id
  role    = "roles/monitoring.metricWriter"
  member  = "serviceAccount:${google_service_account.gke_nodes.email}"
}

resource "google_service_account" "app" {
  account_id   = "${var.app_name}-app"
  display_name = "Seplag Album App SA"
}

resource "google_storage_bucket_iam_member" "app_storage" {
  bucket = google_storage_bucket.albuns.name
  role   = "roles/storage.objectAdmin"
  member = "serviceAccount:${google_service_account.app.email}"
}

resource "google_service_account_iam_member" "workload_identity" {
  service_account_id = google_service_account.app.name
  role               = "roles/iam.workloadIdentityUser"
  member             = "serviceAccount:${var.project_id}.svc.id.goog[seplag-album/seplag-album-sa]"
}
