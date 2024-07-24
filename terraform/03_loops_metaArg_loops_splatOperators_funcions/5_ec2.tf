resource "aws_instance" "ec2-demo" {
  ami           = data.aws_ami.amazon_linux2.id
  instance_type = var.aws_instances_list[0] # For List 
  # instance_type = var.aws_instances_map["prod"] # For Map

  user_data     = file("${path.module}/app1-install.sh")
  key_name      = var.key_name
  vpc_security_group_ids = [
    aws_security_group.allow-ssh.id,
    aws_security_group.allow-web.id
  ]

  # Availaibility Zones using for each loop
  # for_each = toset(data.aws_availability_zones.my_azones.names) # toset() is used to convert list to set
  for_each = toset(keys({
    for az, details in data.aws_ec2_instance_type_offerings.my_inst_type: 
    az => details.instance_types if length(details.instance_types) != 0
  }))
  availability_zone = each.key  # each.value can also be becuase each.key == each.value

  # Create multiple instances using count meta-argument
  # count = 2

  tags = {
    # tags using count meta-argument
    # Name = "ec2-demo-${count.index}"
    Name = "foreach-ec2-demo-${each.key}"
  }
}