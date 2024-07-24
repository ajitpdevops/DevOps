# Get the lates Amazon Linux 2 AMI ID from given region

data "aws_ami" "amazon_linux2" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }
}


# Availability Zones data source
data "aws_availability_zones" "my_azones" {
  filter {
    name = "opt-in-status"
    values = ["opt-in-not-required"]
  }
}