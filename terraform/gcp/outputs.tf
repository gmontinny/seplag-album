output "gke_cluster_name" {
  description = "GKE cluster name"
  value       = google_container_cluster.main.name
}

output "gke_kube_config_command" {
  description = "Command to configure kubectl"
  value       = "gcloud container clusters get-credentials ${google_container_cluster.main.name} --region ${var.region} --project ${var.project_id}"
}

output "cloudsql_connection_name" {
  description = "Cloud SQL connection name"
  value       = google_sql_database_instance.main.connection_name
}

output "cloudsql_private_ip" {
  description = "Cloud SQL private IP"
  value       = google_sql_database_instance.main.private_ip_address
}

output "cloudsql_jdbc_url" {
  description = "JDBC connection URL"
  value       = "jdbc:postgresql://${google_sql_database_instance.main.private_ip_address}:5432/seplag_album"
}

output "gcs_bucket_name" {
  description = "GCS bucket for album covers"
  value       = google_storage_bucket.albuns.name
}

output "artifact_registry_url" {
  description = "Artifact Registry URL"
  value       = "${var.region}-docker.pkg.dev/${var.project_id}/${google_artifact_registry_repository.main.repository_id}"
}

output "app_service_account_email" {
  description = "App GCP service account for Workload Identity"
  value       = google_service_account.app.email
}
