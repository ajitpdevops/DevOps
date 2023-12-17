# ReplicaSets
ReplicaSets are the next-generation Replication Controller. ReplicaSets are used to ensure that a specified number of pod replicas are running at any given time. However, ReplicaSets are not typically used directly, and instead, we use a higher-level abstraction called Deployments.



## Creating a ReplicaSet using yaml
- kubectl create -f replicaset-definition.yml
- kubectl get replicaset
- kubectl describe replicaset
- kubectl delete replicaset myapp-replicaset

## Scaling a ReplicaSet
- kubectl replace -f replicaset-definition.yml
- kubectl scale --replicas=6 -f replicaset-definition.yml
- kubectl scale --replicas=6 replicaset myapp-replicaset

## Editign the replicasets 
- kubectl edit replicaset myapp-replicaset

## Deleting a ReplicaSet
- kubectl delete replicaset myapp-replicaset
