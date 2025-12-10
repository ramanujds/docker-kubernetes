# **Kubernetes Namespaces**

## **What is a Kubernetes Namespace?**
A **Namespace** in Kubernetes is a way to **logically divide** cluster resources. It allows different teams or applications to run in isolation within the **same cluster**.

Think of namespaces like **virtual clusters** inside a **physical cluster**.

---

## **Why Use Namespaces?**
- **Isolation** – Separate workloads for different teams/projects.
- **Resource Management** – Limit CPU/memory per namespace.
- **Access Control** – Implement RBAC (Role-Based Access Control) at the namespace level.
- **Organized Environment** – Separate `dev`, `test`, `prod` workloads.

---

## **Default Namespaces in Kubernetes**
Run:
```sh
kubectl get namespaces
```
You'll see:
```
NAME              STATUS   AGE
default           Active   10d
kube-system       Active   10d
kube-public       Active   10d
kube-node-lease   Active   10d
```

| **Namespace**   | **Purpose** |
|----------------|------------|
| `default`      | Used if no namespace is specified. |
| `kube-system`  | Internal Kubernetes components (e.g., `kube-dns`). |
| `kube-public`  | Public data visible across the cluster. |
| `kube-node-lease` | Manages node heartbeats. |

---

## **Creating a Namespace**
Use `kubectl`:
```sh
kubectl create namespace dev
```
Or using a YAML file:
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: dev
```
Apply it:
```sh
kubectl apply -f namespace.yaml
```

---

## **Deploying Resources in a Namespace**
By default, everything is deployed in the `default` namespace.  
To deploy into a specific namespace, add:
```yaml
metadata:
  namespace: dev
```
Example Deployment:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  namespace: dev
spec:
  replicas: 2
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
        - name: my-app
          image: nginx
```
Apply:
```sh
kubectl apply -f my-app.yaml
```

---

## **Switching Between Namespaces**
Set a default namespace:
```sh
kubectl config set-context --current --namespace=dev
```
Now all `kubectl` commands will run in `dev`.

Check the current namespace:
```sh
kubectl config view --minify | grep namespace
```

---

## **Listing Resources in a Namespace**
```sh
kubectl get pods --namespace=dev
```
Or use shorthand:
```sh
kubectl get pods -n dev
```

---

## How to access resources across all namespaces
To list resources across all namespaces, use the `--all-namespaces` flag:
```sh
kubectl get pods --all-namespaces
```

## How to access resources from one namespace to another
To access resources from one namespace to another, you can specify the namespace in your resource definitions or commands. For example, to access a service in the `dev` namespace from the `default` namespace, you would use the fully qualified domain name (FQDN) of the service:

http://my-service.other-namespace.svc.cluster.local

example:

http://cart-service.dev.svc.cluster.local


## **Deleting a Namespace**
```sh
kubectl delete namespace dev
```
This will delete all resources in that namespace.
