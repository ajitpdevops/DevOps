# Create IAM Role 
# Attach IAM Policy to Role 
# Create Instance Profile 

resource "aws_iam_role" "ec2_ssm_role" {
    name = "${var.environment}-ssm-role"
    assume_role_policy = jsondecode({
        "Version": "2012-10-17"
        "Statement" = {}
    })
  
}

