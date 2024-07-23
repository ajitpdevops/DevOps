# Terraform with AWS 

## Key principles of IaC
- **Idempotency**: Idempotency is a characteristic of certain operations in mathematics, programming languages, and computer science. It refers to the property where applying these operations multiple times produces the same result without altering it except for generating identical copies. In the context of IaC, idempotency means that regardless of the starting state and the number of times the IaC is executed, the end state remains the same. This simplifies the infrastructure provisioning process and minimizes the likelihood of inconsistent outcomes. 

- **Immutability**: Immutable infrastructure is a technique for constructing and managing infrastructure in a dependable, repeatable, and foreseeable manner. This approach offers several advantages over traditional IT environment management methods. Rather than altering the existing infrastructure, immutable infrastructure involves replacing it with a new one.

## Patterns and practices of IaC
- **Source control and VCS** : This practice ensures that you have a record of all changes made to your infrastructure, regardless of how minor they may be. It also simplifies the process of tracking ownership and the history of changes to your infrastructure configurations.
- **Modules and versions** : Creating reusable modules in IaC tools helps with maintenance, readability, and ownership. It keeps changes small and independently deployable and reduces the effect radius. Versioning for modules is quite important to provide support for multiple versions of services that can operate without breaking the existing production resources.
- **Documentation** : IaC minimizes the need for extensive documentation for infrastructure since everything is codified and stated as a declarative manifest. However, some documentation is needed for better infrastructure provisioning so that consumers can understand and improve the current modules and templates.
- **Testing** : Testing is a crucial part of IaC. It ensures that the infrastructure is provisioned as expected and that the changes made to the infrastructure are correct. Testing can be done at different levels, such as unit testing, integration testing, and end-to-end testing.
- **Security and compliance** : IaC can be used by companies for compliance purposes, such as HIPAA, SOX, PCI DSS, and so on. It can also be used for security purposes, such as preventing unauthorized access to data or preventing hackers from accessing sensitive information.
    - **Identity and access management** : Implementing a strong Identity and Access Management (IAM) strategy is essential for safeguarding both your IaC and the infrastructure it provisions. One effective approach is to use Role-Based Access Control (RBAC) for IaC, which can significantly reduce the overall attack surface.
    - **Secrets Management** : It is crucial to ensure that you use a reliable secret management tool, such as HashiCorp Vault or AWS Secrets Manager, to manage these sensitive credentials.
    - **Security scanning** : Leveraging tools such as CIS Benchmarks and Amazon Inspector can be effective in identifying common vulnerabilities or exposures and ensuring adherence to security best practices.
    - **Compliance** : Automation tools such as Chef InSpec or HashiCorp Sentinel can help streamline compliance requirements and improve efficiency. By automating compliance checks, you can run them more frequently and identify issues much faster. 

- 



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

## Integration Terraform with Github Actions and AWS 
- For auth aws-actions/configure-aws-credentials@v4.0.2
- Assume Role with OIDC 
- aws-actions/aws-secretsmanager-get-secrets@v2.0.1 