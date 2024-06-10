-ow do we identify the CPU and Memory requirements for a container?
The CPU and Memory requirements for a container can be identified by running the container and monitoring its resource usage. This can be done using tools like `top`, `htop`, or `docker stats`. Additionally, you can also specify resource limits in the container's configuration file (e.g., Dockerfile or Kubernetes manifest) based on the expected workload and performance requirements.

- How do you scale a Kubernetes deployment?
To scale a Kubernetes deployment, you can use the `kubectl scale` command or update the deployment's replica count in the deployment manifest file. Here are the steps to scale a deployment using `kubectl scale`:

- How to check kubernetes version?
To check the Kubernetes version, you can use the `kubectl version` command. This command will display the client and server versions of Kubernetes that are currently installed on your system.
`eksctl info` - Display information about the cluster

- How do we manage EKS nodes in the Cluster? 
- How do we keep private and public nodes in different node groups? 
- How do we manage private containers go to private nodes and public containers go to public nodes? 
- How to manage the CIDR range and the number of IPs in EKS VPC?
- What is the container runtime in EKS?
containerd is the container runtime used in Amazon EKS. It is an industry-standard core container runtime that provides a reliable and high-performance container runtime environment for Kubernetes workloads.