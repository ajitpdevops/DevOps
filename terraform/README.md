# Terraform with AWS 



## Terraform Modules 
1. Build from Scratch 
2. Leverage existing Terraform Modules

## Terraform State 
1. Local state file
2. Remote State file with S3 
3. State Locking AWS DynamoDB


Installation of Tools 


## AWS
us-east-1
Amazon Linux 2023 AMI 2023.0.20230419.0 x86_64 HVM kernel-6.1
ami-02396cdd13e9a1257

## Learning Day 1:

Understand basic Terraform Commands
1. terraform init
2. terraform validate
3. terraform plan
4. terraform apply
5. terraform destroy
    

Understand Terraform Language Basics
1. Understand Top Level Blocks
    - Terraform settings block
    - Provider Block
    - Resources 
    - Input Variable Block
    - Output Values 
    - Local values 
    - Data Sources 
    - Module 
2. Understand Arguments, Attributes & Meta-Arguments
    - Arguments 
    - Attributes 
    - Meta-Arguments | providers, for each | count 
3. Understand Identifiers
    - 
4. Understand Comments

    ```t
        # Template
        <BLOCK TYPE> "<BLOCK LABEL>" "<BLOCK LABEL>"   {
        # Block body
        <IDENTIFIER> = <EXPRESSION> # Argument
        }

        # AWS Example
        resource "aws_instance" "ec2demo" { # BLOCK
        ami           = "ami-04d29b6f966df1537" # Argument
        instance_type = var.instance_type # Argument with value as expression (Variable value replaced from varibales.tf
        }
    ```
# Usage 
1. terraform block 
2. provider block 
3. resource block 
4. terraform plan 
5. terraform state file
6. terraform plan -destroy 

