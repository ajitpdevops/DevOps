# IAM Role for SSM
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


# https://aws.amazon.com/blogs/containers/amazon-ebs-csi-driver-is-now-generally-available-in-amazon-eks-add-ons/ 
data "aws_iam_policy" "ebs_csi_policy" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonEBSCSIDriverPolicy"
}

module "irsa-ebs-csi" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-assumable-role-with-oidc"
  version = "5.39.0"

  create_role                   = true
  role_name                     = "AmazonEKSTFEBSCSIRole-${module.eks.cluster_name}"
  provider_url                  = module.eks.oidc_provider
  role_policy_arns              = [data.aws_iam_policy.ebs_csi_policy.arn]
  oidc_fully_qualified_subjects = ["system:serviceaccount:kube-system:ebs-csi-controller-sa"]
}

# # Define the addtional IAM policies for the EKS cluster

# resource "aws_iam_policy" "additional_eks_permissions" {
#   name        = "additional-eks-permissions"
#   description = "Additional permissions for the EKS cluster"
#   policy      = jsonencode({
#     "Version": "2012-10-17",
#     "Statement": [
#       {
#         "Effect": "Allow",
#         "Action": [
#           "ec2:AttachVolume",
#           "ec2:DetachVolume"
#         ],
#         "Resource": "*"
#       }
#     ]
#   })
# }

# resource "aws_iam_policy_attachment" "eks_node_group_additional_permissions" {
#   # Attach the policy to the managed node groups
#   for_each = toset([for ng in values(module.eks.eks_managed_node_groups) : ng.iam_role_arn])
  
#   roles = [each.value]
#   policy_arn = aws_iam_policy.additional_eks_permissions.arn
#   name = "additional-eks-permissions-attachment-${each.key}"

# }