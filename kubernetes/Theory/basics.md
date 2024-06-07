# Components 
- API Server            : Frontend the users, mdmt devices, CLI talks to K8s 
- etcd                  : Distributed, reliable key-value store to all data used to manage the cluster, implementing 
- kubelet               : is the agent that runs on each node 
- container runtime     : is the underlying software that is used to run containers.
- controller            : The controllers are the brain behind orchestration. They are responsible for noticing and responding when nodes, containers or end points goes down.
- Scheduller            :  The Scheduler is responsible for distributing work or containers across multiple nodes. It looks for newly created containers and assigns them to nodes.
