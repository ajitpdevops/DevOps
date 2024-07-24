variable "aws_region" {
  description = "value of the region"
  type = string
  default = "us-east-1"
}

variable "instance_type" {
  description = "value of the instance type"
  type = string
  default = "t2.micro"
}

variable "key_name" {
  description = "value of the key name"
  type = string
  default = "terraform-key"
}

# AWS Instances in List Type
variable "aws_instances_list" {
  description = "value of the aws instances"
  type = list(string)
  default = ["t3.micro", "t2.small", "t2.medium"]
}

# AWS Instances for env in map type
variable "aws_instances_map" {
  description = "value of the aws instances"
  type = map(string)
  default = {
    dev = "t3.micro"
    qa = "t2.small"
    prod = "t2.medium"
  }
}