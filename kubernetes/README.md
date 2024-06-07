## Kubernetes Learning Path 
- Basics of Kuberenetes 
- Pods 
- Services 
- ReplicaSets
- Deployments 
- Networking and Service Discovery 
- Microservices architecture 
- Kubernetes Persistance and Volumes 
- Running EKS on AWS


## K8s commands - 
```bash
kubectl version --client
minikube version
minikube config set driver docker
minikube start
minikube status
minikube ip
kubectl get po -A
kubectl get po
kubectl get po -A
minikube kubectl --get pods
minikube kubectl -- get pods
minikube kubectl -- --help
kubectl --help
kubectl get pods
minikube kubectl -- get pods
minikube dashboard
minikube status
minikube stop
minikube status
minikube delete
minikube status
minikube help
minikube start --help
minikube stop
kubectl version
minikube start --memory 4096m
kubectl version
kubectl get po -A
kubectl get deployments
kubectl get po
kubectl get all
kubectl apply -f first-pod.yml 
kubectl describe pod webapp
kubectl delete po webapp webapp-release0-5
kubectl stop po webapp-7hncb
kubectl rollout status deployment webapp
kubectl rollout history deploy webapp
kubectl rollout undo deploy webapp
```

## Resources 
microservices.io/patterns/apigateway 

## Images 
richardchesterwood/k8s-fleetman-queue:release1
richardchesterwood/k8s-fleetman-position-simulator:release1
richardchesterwood/k8s-fleetman-position-tracker:release1
richardchesterwood/k8s-fleetman-api-gateway:release1
richardchesterwood/k8s-fleetman-webapp-angular:release0-5

