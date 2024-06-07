# Output instance public DNS with for loop with List 

output "aws_public_dns_list" {
  description = "List of the aws public dns"
  value = [for instance in aws_instance.ec2-demo: instance.public_dns]
}

# Output instance public DNS with for loop with Map

output "aws_public_dns_map" {
  description = "Map of the aws public dns"
  value = { for instance in aws_instance.ec2-demo: instance.tags.Name => instance.public_dns}
}

# Output - Loop with Map - Advanced

output "aws_public_dns_map2" {
  description = "Map2 of the aws public dns"
  value = { for c, instance in aws_instance.ec2-demo: c => instance.public_dns}
}

# Legacy splat operator 
output "legacy_splat_instance_publicdns" {
  description = "Legacy splat operator of the aws public dns"
  # value = aws_instance.ec2-demo.*.public_dns # We cannot work with Splat operator when using foreach becuase it's not a list
  value = toset([for instance in aws_instance.ec2-demo: instance.public_dns])
}

# Latest generalised splat operator
output "latest_splat_instance_publicdns" {
  description = "Latest splat operator of the aws public dns"
  # value = aws_instance.ec2-demo[*].public_dns
  value = tomap({for az, instance in aws_instance.ec2-demo: az => instance.public_dns})
}