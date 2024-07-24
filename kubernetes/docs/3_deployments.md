# Deployments 
Kubernetes deployments are a high-level abstraction that can be used to deploy and manage applications. Deployments use ReplicaSets under the hood to manage the deployment of pods. Deployments are the recommended way to manage the creation and scaling of pods.

## Creating a deployment using yaml
- kubectl create -f deployment-definition.yml
- kubectl get deployments
- kubectl describe deployment <name_of_deployment>
- kubectl get all 

## Updating a deployment
- kubectl apply -f deployment-definition.yml
- kubectl set image deployment/myapp-deployment nginx=nginx:1.9.1
- kubectl rollout status deployment/myapp-deployment
- kubectl rollout history deployment/myapp-deployment
- kubectl rollout undo deployment/myapp-deployment
- kubectl rollout undo deployment/myapp-deployment --to-revision=2

