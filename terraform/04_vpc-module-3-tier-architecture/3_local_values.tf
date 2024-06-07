locals {
  owners = var.business_division
  environment = var.environment
  name = "${var.environment}-${var.business_division}"

  common_tags = {
    Terraform   = "true"
    owners = local.owners
    environment = local.environment
  }
}