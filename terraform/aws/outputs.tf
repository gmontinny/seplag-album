output "eks_cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "eks_cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = module.eks.cluster_endpoint
}

output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint"
  value       = aws_db_instance.postgres.endpoint
}

output "rds_jdbc_url" {
  description = "JDBC connection URL"
  value       = "jdbc:postgresql://${aws_db_instance.postgres.endpoint}/seplag_album"
}

output "s3_bucket_name" {
  description = "S3 bucket for album covers"
  value       = aws_s3_bucket.albuns.id
}

output "ecr_repository_url" {
  description = "ECR repository URL"
  value       = aws_ecr_repository.app.repository_url
}

output "app_iam_role_arn" {
  description = "IAM role ARN for the app ServiceAccount (IRSA)"
  value       = module.irsa_app.iam_role_arn
}
