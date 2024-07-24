module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.8.1"

  name = "${local.cluster_name}-vpc"

  cidr = var.vpc_cidr
  azs  = local.availibility_zones

  private_subnets = var.private_subnet_cidrs
  public_subnets  = var.public_subnet_cidrs

  enable_nat_gateway   = var.enable_nat_gateway
  single_nat_gateway   = var.single_nat_gateway
  enable_dns_hostnames = var.enable_dns_hostnames

  tags = local.common_tags

  vpc_tags = local.common_tags

  public_subnet_tags = {
    "kubernetes.io/role/elb" = 1
    "kubernetes.io/cluster/${local.cluster_name}" = "shared"
  }

  private_subnet_tags = {
    "kubernetes.io/role/internal-elb" = 1
    "kubernetes.io/cluster/${local.cluster_name}" = "shared"
  }
}

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "20.8.5"

  cluster_name    = local.cluster_name
  cluster_version = "1.29"

  cluster_endpoint_public_access           = true
  enable_cluster_creator_admin_permissions = true

  cluster_addons = {
    aws-ebs-csi-driver = {
      service_account_role_arn = module.irsa-ebs-csi.iam_role_arn
    }
  }

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  eks_managed_node_group_defaults = {
    ami_type = "AL2_x86_64"

  }

  eks_managed_node_groups = {
    one = {
      name = "node-group-1"

      instance_types = ["t3.medium"]
      # capacity_type  = "SPOT"

      min_size     = 1
      max_size     = 3
      desired_size = 2

    }

    two = {
      name = "node-group-2"

      instance_types = ["t3.medium"]
      # capacity_type  = "SPOT"

      min_size     = 1
      max_size     = 2
      desired_size = 1
    }
  }
  tags = {
    Environment = "education"
    Terraform   = "true"
  }
  iam_role_additional_policies = {
    "AmazonEC2RoleforSSM" = "arn:aws:iam::aws:policy/service-role/AmazonEC2RoleforSSM"
  }
}


