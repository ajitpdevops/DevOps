# Resources - 
- https://www.youtube.com/watch?v=cipDJwDWWbY | A deep dive into Amazon EKS 
- 

# Create Cluster
eksctl create cluster --name=eksdemocluster \
                      --region=us-east-1 \
                      --zones=us-east-1a,us-east-1b \
                      --version=1.28 \
                      --without-nodegroup

# Get List of clusters
eksctl get cluster                  


# Create & Associate IAM OIDC Provider for our EKS Cluster
eksctl utils associate-iam-oidc-provider \
    --region us-east-1 \
    --cluster eksdemocluster \
    --approve

# Create EC2 Keypair [Not required if SSM Enabled]
rm eksdemocluster.pem
aws ec2 create-key-pair --key-name eksdemocluster --query 'KeyMaterial' --output text > eksdemocluster.pem


# Create Public Node Group   
eksctl create nodegroup --cluster=eksdemocluster \
                       --region=us-east-1 \
                       --name=eksdemocluster-ng-public1 \
                       --node-type=t3.medium \
                       --nodes=2 \
                       --nodes-min=2 \
                       --nodes-max=4 \
                       --node-volume-size=20 \
                       --enable-ssm \
                       --managed \
                       --asg-access \
                       --external-dns-access \
                       --full-ecr-access \
                       --appmesh-access \
                       --alb-ingress-access 



eksctl create iamserviceaccount --name ebs-csi-controller-sa \
    --namespace kube-system \
    --cluster eksdemocluster \
    --attach-policy-arn arn:aws:iam::aws:policy/service-role/AmazonEBSCSIDriverPolicy \
    --role-name AmazonEKS_EBS_CSI_DriverRole \
    --approve  \
    --role-only  

eksctl create addon --name aws-ebs-csi-driver \
    --cluster eksdemocluster \
    --service-account-role-arn arn:aws:iam::$(aws sts get-caller-identity --query Account --output text):role/AmazonEKS_EBS_CSI_DriverRole \
    --force    
                

# Optional - update the `kubeconfig` file 
aws eks update-kubeconfig --name eksdemocluster --region us-east-1 

# List EKS clusters
eksctl get cluster

# List NodeGroups in a cluster
eksctl get nodegroup --cluster eksdemocluster

# List Nodes in current kubernetes cluster
kubectl get nodes -o wide

# Our kubectl context should be automatically changed to new cluster
kubectl config view --minify    

# List EKS Clusters
eksctl get clusters


# Delete Node Group
eksctl delete nodegroup --cluster=eksdemocluster --name=eksdemocluster-ng-public1

# Delete Cluster
eksctl delete cluster --name=eksdemocluster --region=us-east-1