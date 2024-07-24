variable "aws_region" {
  default = "us-east-2"
}

variable "environment" {
  default = "dev"
}

variable "service_name" {
  description = "value of the service name"
  type        = string
}

### VPC Variables: START 
variable "vpc_cidr_block" {
  default = "10.0.0.0/16"
}

variable "public_subnet_cidr_block" {
  default = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]

}

variable "private_subnet_cidr_block" {
  default = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

variable "availability_zones" {
  default = ["us-east-2a", "us-east-2b", "us-east-2c"]
}

### VPC Variables: END
