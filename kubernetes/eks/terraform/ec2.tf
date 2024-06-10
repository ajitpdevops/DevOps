data "aws_ami" "windows_server_2022" {
  most_recent = true
  owners = ["amazon"]
  filter {
    name = "name"
    values = ["Windows_Server-2022-English-Full-Base-*"]
  }
  filter {
    name = "architecture"
    values = ["x86_64"]
  }
  filter {
    name = "virtualization-type"
    values = ["hvm"]
  }
}


resource "aws_iam_role" "ssm_role" {
  name = "ec2-ssm-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_role_policy_attachment" {
  role       = aws_iam_role.ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

# IAM Instance Profile
resource "aws_iam_instance_profile" "ssm_instance_profile" {
  name = "ec2-ssm-instance-profile"
  role = aws_iam_role.ssm_role.name
}

# EC2 Instance

resource "aws_instance" "windows_bastion" {
  ami           = data.aws_ami.windows_server_2022.id
  instance_type = "t3.medium"
  
  iam_instance_profile = aws_iam_instance_profile.ssm_instance_profile.name
  subnet_id = module.vpc.private_subnets[0]
  security_groups = [module.eks.node_security_group_id]
  
  tags = {
    Name = "WindowsBastion2022WithSSM"
  }

  depends_on = [ module.eks, module.vpc ]
}