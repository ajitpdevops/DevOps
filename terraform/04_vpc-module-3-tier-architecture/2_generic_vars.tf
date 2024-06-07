variable "aws_region" {
    description = "Value of the AWS Region"
  default = "us-east-2"
}


variable "environment" {
  description = "Environment name used a sprefix"
  default = "dev"
}
  
variable "business_division" {
  description = "Business Division for Large organization"
  default = "devops"
}