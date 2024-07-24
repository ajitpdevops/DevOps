# Ingress 

**Ingress** is a Kubernetes resource that allows you to configure an HTTP load balancer for your Kubernetes services. Such a load balancer is necessary to expose your services to the internet. Ingress is a Layer 7 (HTTP) load balancer, whereas Service is a Layer 4 (TCP) load balancer. Ingress is a collection of rules that allow inbound connections to reach the cluster services. It can be configured to give services externally-reachable URLs, load balance traffic, terminate SSL, offer name-based virtual hosting, and more. Ingress can provide HTTP and HTTPS routing. It is a powerful resource that allows you to configure a lot of things that would otherwise require a separate load balancer.

# How to get ingress running with minikube 
- Start minikube
- Enable the ingress addon: `minikube addons enable ingress`
- Create an ingress resource: `kubectl apply -f example-ingress.yaml`
- Check ingress resource: `kubectl get ingress`