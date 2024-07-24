
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