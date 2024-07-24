resource "random_string" "suffix" {
  length  = 8
  special = false
}

locals {
    cluster_name = "${var.service_name}-${var.environment}-${random_string.suffix.result}"
    availibility_zones = slice(data.aws_availability_zones.available.names, 0, 3)

    common_tags = {
        Environment = var.environment
        Service     = var.service_name
    }
}