# Helm 

Helms is a package manager for Kubernetes. It is a tool that streamlines installing and managing Kubernetes applications. Think of it like apt/yum/homebrew for Kubernetes.

helm3 is the latest version of helm. It is a major upgrade from helm2.
helm3 is a client only version of helm. It does not have a server component like tiller in helm2.
helm3 is more secure than helm2. It does not have the security issues that helm2 had.

## Installation

## Important Commands

helm version
helm search repo prometheus-community
helm list
helm status

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm install monitoring prometheus-community/kube-prometheus-stack

helm show values prometheus-community/kube-prometheus-stack

helm upgrade monitoring prometheus-community/kube-prometheus-stack --set grafana.adminPassword=admin

helm upgrade monitoring prometheus-community/kube-prometheus-stack --values values.yaml

# Helm Charts
helm pull prometheus-community/kube-prometheus-stack
helm pull prometheus-community/kube-prometheus-stack --untar=true