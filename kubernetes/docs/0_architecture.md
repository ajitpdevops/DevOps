# Kubernetes Architecture

Kubernetes follows a client-server architecture. The Kubernetes API server acts as a gateway to the cluster, and all operations must go through it. The API server is the only component that can communicate with the etcd database. The API server is also the only component that can communicate with the kubelet agents on each node. The kubelet agents are responsible for managing the containers running on the node.

## K8s Cluster Components
- Master Node
    - API Server : Frontend for the kubernetes control plane
    - Scheduler : Responsible for distributing work or containers across multiple nodes
    - Controller Manager : 
        - Node Controller : Responsible for noticing and responding when nodes go down.
        - Replication Controller : Responsible for maintaining the correct number of pods for every replication controller object in the system.
        - Endpoints Controller : Populates the Endpoints object (that is, joins Services & Pods).
        - Service Account & Token Controllers : Create default accounts and API access tokens for new namespaces.
    - etcd : is a distributed reliable key-value store used by kubernetes to store all data used to manage the cluster

- Worker Node
    - Kubelet : Responsible for communication between the master node and the worker node. It also manages the pods and containers running on a machine.
    - Kube-proxy : Responsible for network proxy and load balancing. It also enables the kubernetes service abstraction by maintaining network rules on the host and performing connection forwarding.
    - Container Runtime : Responsible for pulling the container image from a registry, unpacking the container, and running the application.

## Kubectl - CLI tool to interact with the cluster
    - kubectl run hello-minikube --image=k8s.gcr.io/echoserver:1.4 --port=8080
    - kubectl cluster-info
    - kubectl get nodes

# Kubernetes Objects
- Kubernetes objects are persistent entities in the Kubernetes system. Kubernetes uses these entities to represent the state of your cluster. Specifically, they can describe:
    - What containerized applications are running (and on which nodes)
    - The resources available to those applications
    - The policies around how those applications behave, such as restart policies, upgrades, and fault-tolerance


# YAML File structure

- kubernetes uses YAML files to define and create objects. YAML is a human-readable language that is used as the format for configuration files in many applications. All yml files must have the following structure:

```
apiVersion:
kind:
metadata:


spec: 
```