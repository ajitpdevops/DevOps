data "aws_ami" "amazon_linux2" {
    most_recent = true

    owners = [ "Amazon" ]

    filter {
      name = 
    }
}