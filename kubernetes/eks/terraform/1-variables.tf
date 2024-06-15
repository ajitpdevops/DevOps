## Service Variables
variable "region" {
  description = "AWS region"
  type        = string
  default     = "us-east-2"
}

variable "environment" {
  description = "Environment for the resources"
  type        = string
  default     = "dev"
}

variable "service_name" {
  description = "Service name"
  type = string
  default = "fleetops"
}

## VPC Variables
variable "vpc_cidr" {
  description = "CIDR for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDRs for the public subnets"
  type        = list(string)
  default     = ["10.0.10.0/24", "10.0.11.0/24", "10.0.12.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDRs for the private subnets"
  type        = list(string)
  default     = ["10.0.20.0/24", "10.0.21.0/24", "10.0.22.0/24"]
}

variable "enable_nat_gateway" {
  description = "Enable NAT Gateway"
  type        = bool
  default     = true 
}

variable "single_nat_gateway" {
  description = "Single NAT Gateway"
  type        = bool
  default     = true
}

variable "enable_dns_hostnames" {
  description = "Enable DNS Hostnames"
  type        = bool
  default     = true
}

# # VPC Create Database Subnet Group (True / False)
# variable "vpc_create_database_subnet_group" {
#   description = "VPC Create Database Subnet Group"
#   type = bool
#   default = true 
# }

# # VPC Create Database Subnet Route Table (True or False)
# variable "vpc_create_database_subnet_route_table" {
#   description = "VPC Create Database Subnet Route Table"
#   type = bool
#   default = true   
# }

## EKS Variables
variable "eks_version" {
  description = "EKS version"
  type        = string
  default     = "1.28"
}

variable "cluster_creator_admin_permissions" {
  description = "Enable cluster creator admin permissions"
  type        = bool
  default     = true
}