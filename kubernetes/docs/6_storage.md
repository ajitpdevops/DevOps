# Storage in Kubernetes
Kubernetes provides a number of storage options for your applications. These include local storage, network storage, and cloud storage. In this section, we will discuss the different storage options available in Kubernetes.

## Persistent Volumes (PVs)
Persistent Volumes (PVs) are a way to provision storage in a Kubernetes cluster. They are resources in the cluster just like pods and services. PVs are volume plugins like Volumes, but have a lifecycle independent of any individual pod that uses the PV. This API object captures the details of the implementation of the storage, be that NFS, iSCSI, or a cloud-provider-specific storage system.

## Persistent Volume Claims (PVC)
Each PVC contains a spec and status, which is the specification and status of the claim. The name of a PersistentVolumeClaim object must be a valid DNS subdomain name.