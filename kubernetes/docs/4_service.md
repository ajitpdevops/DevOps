# Service in Kubernetes
In Kubernetes, a Service is a method for exposing a network application that is running as one or more Pods in your cluster.Service in kubernetes is an abstraction layer which defines a logical set of pods and a policy by which to access them. It enable a loose coupling between dependent pods. A service is defined using YAML (preferred) or JSON, like all Kubernetes objects.

A key aim of Services in Kubernetes is that you don't need to modify your existing application to use an unfamiliar service discovery mechanism. You can run code in Pods, whether this is a code designed for a cloud-native world, or an older app you've containerized. You use a Service to make that set of Pods available on the network so that clients can interact with it.


Services can be exposed in different ways by specifying a type in the service specification. The different types of services are:
- NodePort : Exposes the service on each Node’s IP at a static port (the NodePort). A ClusterIP service, to which the NodePort service will route, is automatically created. You’ll be able to contact the NodePort service, from outside the cluster, by requesting <NodeIP>:<NodePort>.
- ClusterIP : Exposes the service on a cluster-internal IP. Choosing this value makes the service only reachable from within the cluster. This is the default ServiceType.
- LoadBalancer : Exposes the service externally using a cloud provider’s load balancer. NodePort and ClusterIP services, to which the external load balancer will route, are automatically created.
- ExternalName : Maps the service to the contents of the externalName field (e.g. foo.bar.example.com), by returning a CNAME record with its value. No proxying of any kind is set up.

## Creating a service using yaml
- kubectl create -f service-definition.yml
- kubectl get services
- kubectl describe service <name_of_service>
- kubectl get all
- kubectl edit service <name_of_service>
- kubectl port-forward svc/<name_of_service> <local_port>:<service_port>
- kubectl delete service <name_of_service>