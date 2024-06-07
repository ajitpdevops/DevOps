# MiniKube 
MiniKube is a tool that makes it easy to run Kubernetes locally. MiniKube runs a single-node Kubernetes cluster inside a VM on your laptop for users looking to try out Kubernetes or develop with it day-to-day.

## MiniKube Commands
- minikube start | minikube start --memory 4096m
- minikube status
- minikube stop
- minikube delete
- minikube dashboard
- minikube ssh
- minikube addons list
- minikube addons enable <addon-name>
- minikube addons disable <addon-name>
- minikube ip
- minikube logs
- minikube pause
- minikube unpause
- minikube update-check
- minikube update
- minikube version
- minikube config set memory 8192
- minikube config set cpus 4
- minikube config set vm-driver docker
- minikube config set kubernetes-version v1.20.2
- minikube config set disk-size 50GB
- minikube config set memory 8192
- minikube service myapp-service --url
- minikube service myapp-service
- minikube service list
- minikube service list --namespace kube-system
- kubectl get po -A
