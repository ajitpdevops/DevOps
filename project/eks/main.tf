module "eks_vpc" {
  source = "terraform-aws-modules/vpc/aws"

  name = var.service_name

  cidr = var.vpc_cidr_block

  azs             = var.availability_zones
  private_subnets = var.private_subnet_cidr_block
  public_subnets  = var.public_subnet_cidr_block

  enable_nat_gateway = true

  tags = {
    "Terraform"   = "true"
    "Environment" = "{$var.environment}"
  }
}
