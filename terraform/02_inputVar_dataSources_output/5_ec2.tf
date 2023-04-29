resource "aws_instance" "ec2-demo" {
  ami           = data.aws_ami.amazon_linux2.id
  instance_type = var.instance_type
  user_data     = file("${path.module}/app1-install.sh")
#   key_name      = var.key_name
  subnet_id     = "subnet-076280e5b4c1540e5"
  security_groups = [aws_security_group.allow-ssh.id, aws_security_group.allow-web.id]

  tags = {
    Name = "ec2-demo"
  }
}