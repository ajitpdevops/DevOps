```bash
create a s3 bucket for kops to save state file


aws s3api create-bucket \
    --bucket us-east-1-fleetmanbucket1 \
    --region us-east-1

aws s3api put-bucket-versioning --bucket us-east-1-fleetmanbucket1 --versioning-configuration Status=Enabled

export NAME=myfirstcluster.k8s.local
export KOPS_STATE_STORE=s3://us-east-1-fleetmanbucket1

ssh-keygen -b 2048 -t rsa ~/.ssh/id_rsa

kops create cluster \
    --name ${NAME} \
    --state ${KOPS_STATE_STORE} \
    --zones us-east-1a,us-east-1b,us-east-1c \
    --node-count 2 \
    --node-size t3.medium \
    --control-plane-size t3.medium \
    --dns-zone k8s.local

kops create cluster \
    --name ${NAME} \
    --state ${KOPS_STATE_STORE} \
    --zones us-east-1a,us-east-1b,us-east-1c \
    --node-count 2 \
    --node-size t3.medium \
    --master-size t3.medium \
    --dns-zone k8s.local  \
    --out=. \
    --target=terraform

kops edit cluster --name ${NAME}
kops update cluster --name ${NAME} --yes --admin=87600h
kops get ig --name ${NAME}
kops edit ig nodes-us-east-1a
kops edit instancegroup --name k8s-cluster.example.com nodes --state=s3://my-state-store

kops export kubeconfig --admin=87600h

kops delete cluster cluster2.kubernetes-aws.io --state=s3://kubernetes-aws-io --yes


```

```md
kOps has set your kubectl context to myfirstcluster.k8s.local

Terraform output has been placed into .
Run these commands to apply the configuration:
   cd .
   terraform plan
   terraform apply

Suggestions:
 * validate cluster: kops validate cluster --wait 10m
 * list nodes: kubectl get nodes --show-labels
 * ssh to a control-plane node: ssh -i ~/.ssh/id_rsa ubuntu@api.myfirstcluster.k8s.local
 * the ubuntu user is specific to Ubuntu. If not using Ubuntu please use the appropriate user based on your OS.
 * read about installing addons at: https://kops.sigs.k8s.io/addons.
 ```