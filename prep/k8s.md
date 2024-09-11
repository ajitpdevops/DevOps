# AWS Specific Questions

- How do we manage VPC in AWS?

  - Subnets
  - Route Tables
  - IGW
  - NAT Gateway
  - NAT Instances
  - EIP
  - Network ACL
  - Security Groups
  - VPC Peering
  - Direct Connect
  -

- How do we manage SSO in AWS?
- Hosting zones in AWS?
- Landing Zones in AWS?
- AWS SSM and its advantages?

# Kubernetes Specific Questions

### How do we identify the CPU and Memory requirements for a container?

The CPU and Memory requirements for a container can be identified by running the container and monitoring its resource usage. This can be done using tools like `top`, `htop`, or `docker stats`. Additionally, you can also specify resource limits in the container's configuration file (e.g., Dockerfile or Kubernetes manifest) based on the expected workload and performance requirements.

### How do you scale a Kubernetes deployment?

To scale a Kubernetes deployment, you can use the `kubectl scale` command or update the deployment's replica count in the deployment manifest file. Here are the steps to scale a deployment using `kubectl scale`:

### How to check kubernetes version?

To check the Kubernetes version, you can use the `kubectl version` command. This command will display the client and server versions of Kubernetes that are currently installed on your system.
`eksctl info` - Display information about the cluster

- How do we manage EKS nodes in the Cluster?
- How do we keep private and public nodes in different node groups?
- How do we manage private containers go to private nodes and public containers go to public nodes?
- How to manage the CIDR range and the number of IPs in EKS VPC?
- What is the container runtime in EKS?
  containerd is the container runtime used in Amazon EKS. It is an industry-standard core container runtime that provides a reliable and high-performance container runtime environment for Kubernetes workloads.

### What are the advantages of using Terraform with EKS over eksctl?

Terraform provides more flexibility and customization options compared to eksctl. With Terraform, you can define your infrastructure as code using HCL (HashiCorp Configuration Language) and manage the entire lifecycle of your EKS cluster and associated resources. Terraform also allows you to integrate with other cloud providers and services, making it a more versatile tool for managing infrastructure.

### Can you give me an example of how you would create AWS & EKS infrastructure with Terraform, then manage you manifest with helm and using Jenkins for Deployment?

Sure! Here is an example workflow for creating AWS & EKS infrastructure with Terraform, managing manifests with Helm, and using Jenkins for deployment:

Sure, here's a high-level example of how you might set up this workflow.

Terraform: You would use Terraform to provision your AWS and EKS infrastructure. Here's a basic example of what the Terraform files might look like:

```bash
provider "aws" {
  region = "us-west-2"
}

module "eks" {
  source          = "terraform-aws-modules/eks/aws"
  cluster_name    = "my-eks-cluster"
  cluster_version = "1.20"
  subnets         = ["subnet-abcde012", "subnet-bcde012a", "subnet-fghi345a"]
  vpc_id          = "vpc-abcde012"
}

data "aws_ami" "eks_worker" {
  filter {
    name   = "name"
    values = ["amazon-eks-node-v*"]
  }

  most_recent = true
  owners      = ["602401143452"] # Amazon
}

module "eks_node_group" {
  source          = "terraform-aws-modules/eks/aws"
  cluster_name    = module.eks.cluster_id
  node_group_name = "eks-worker-group"
  node_role_arn   = module.eks.worker_iam_role_arn
  ami_id          = data.aws_ami.eks_worker.id
  instance_type   = "m4.large"
  desired_capacity = 3
  min_capacity     = 1
  max_capacity     = 4
}
```

Helm: You would use Helm to manage your Kubernetes manifests. Here's an example of a Helm chart for a simple web application:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: { { .Values.service.name } }
  labels:
    app: { { .Values.service.name } }
spec:
  type: LoadBalancer
  ports:
    - port: 80
      targetPort: 8080
      protocol: TCP
      name: http
  selector:
    app: { { .Values.service.name } }
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: { { .Values.deployment.name } }
spec:
  replicas: { { .Values.deployment.replicaCount } }
  selector:
    matchLabels:
      app: { { .Values.service.name } }
  template:
    metadata:
      labels:
        app: { { .Values.service.name } }
    spec:
      containers:
        - name: { { .Values.container.name } }
          image: { { .Values.container.image } }
          ports:
            - containerPort: 8080
```

Jenkins: You would use Jenkins to automate the deployment process. Here's an example of a Jenkinsfile that deploys the Helm chart:

```bash
pipeline {
  agent any
  stages {
    stage('Deploy') {
      steps {
        sh 'helm upgrade --install my-app ./my-app'
      }
    }
  }
}
```

This is a very basic example and your actual setup would likely be more complex. You would need to customize these examples to fit your specific needs.

### What are the AWS DR (Disaster REcovery) strategies?

- Backup & Restore (RPO/RTO hours)
  - Low priority use case
  - Provision all AWS resources after event
  - Restore backups after events
- Pilot Light (RPO/RTO 10 minutes)
  - Data Live
  - Service idle
  - Provision Some AWS resources and scale after event
- Warm Standby (RPO/RTO MInutes)
  - Always runnign but smaller
  - Business Critical
  - Scale Resources after disaster event
- Multi-Site Active/Active (Real-Time)
  - Zero Downtime
  - Near Zero Data Loss
  - Mission Critical Services

## How do prepare for the Big Day Spike?

- Pre-warm Load balancers
- Scheulded autoscaling
- Keep AMIs lightwieght
- Run IEM (Infra Event Mgmt) to handle high traffic
- Use Database proxy

## How do handle the same problem with Microservices in EKS?

- Overprovisioner
- Reducing container image size

## What is Helm in Kubernetes?

Helm is a package manager for Kubernetes, which helps you define, install, and upgrade even the most complex Kubernetes applications. Helm uses a packaging format called charts, which are collections of files that describe a related set of Kubernetes resources. It simplifies the management of Kubernetes applications by providing a way to deploy, configure, and manage applications through reusable and shareable charts.

## What are Helm charts?

Helm charts are packages of pre-configured Kubernetes resources. A chart is a collection of files that describe a related set of Kubernetes resources. They include:

`Chart.yaml`: Contains metadata about the chart.
`values.yaml`: The default configuration values for the chart.
`Templates` directory: Contains templates that, when combined with values, generate Kubernetes manifest files.
`requirements.yaml`: Lists dependencies for the chart. Charts can be stored locally or in a chart repository.

## Helm Commands

```bash
helm install myrelease myrepo/mychart
helm install myrelease ./mychart
helm install myrelease myrepo/mychart -f custom-values.yaml
helm upgrade myrelease myrepo/mychart
helm upgrade myrelease myrepo/mychart -f custom-values.yaml
helm create mychart
helm list
helm list --namespace mynamespace
helm list --all-namespaces
helm rollback myrelease 1
helm history myrelease

```

### What are helm hooks?

Helm hooks allow you to intervene at specific points in a release lifecycle, such as before or after installing, upgrading, or deleting a release. Hooks are defined in the chart templates using special annotations.
Common hooks include pre-install, post-install, pre-upgrade, post-upgrade, pre-delete, and post-delete.

For example:

```yml
apiVersion: batch/v1
kind: Job
metadata:
  name: pre-install-job
  annotations:
    "helm.sh/hook": pre-install
spec:
  template:
    spec:
      containers:
        - name: job
          image: busybox
          command: ["sh", "-c", "echo Hello from the pre-install hook!"]
      restartPolicy: Never
```
