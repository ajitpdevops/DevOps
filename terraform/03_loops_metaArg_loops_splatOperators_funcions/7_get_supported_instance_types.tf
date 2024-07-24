
# Data Source 2 
data "aws_ec2_instance_type_offerings" "my_inst_type" {
  for_each = toset(data.aws_availability_zones.my_azones.names)
  filter {
    name = "instance-type"
    values = ["t3.micro"]
  }

  filter {
    name = "location"
    values = [each.key]
  }

  location_type = "availability-zone"
}


# Output 1 : Map with AZ & instanace Type if supported

output "output_v_1" {
  value = {
    for az, details in data.aws_ec2_instance_type_offerings.my_inst_type: az => details.instance_types
  }
}


# Output 2 : List of instance types if supported

output "output_v_2" {
  value = {
    for az, details in data.aws_ec2_instance_type_offerings.my_inst_type: 
    az => details.instance_types if length(details.instance_types) != 0
  }
}
  
# Output 3 : List of instance types if supported with AZ

output "output_v_3" {
  value = keys({
    for az, details in data.aws_ec2_instance_type_offerings.my_inst_type: 
    az => details.instance_types if length(details.instance_types) != 0
  })
}

# Output 4 : List of instance types if supported with AZ

output "output_v_4" {
    value = keys({
        for az, details in data.aws_ec2_instance_type_offerings.my_inst_type: 
        az => details.instance_types if length(details.instance_types) != 0
    })[0]
    }