
## Important Notes on Kubernetes

---

### Why can't I access `minikube ip` from my local machine directly?

- Minikube runs in a **Docker container**, *not* a full VM.
- This container is on a **Docker network bridge**, which is *not directly exposed* to your host.
- The IP from `minikube ip` is **internal to Docker**, and your host can’t route to it directly.
- Use this instead:

  ```bash
  minikube service <svc-name>
  ```

  This command creates a **temporary tunnel** to expose the service on your local machine.

---

### How to access the terminal for a pod?

```bash
kubectl exec -it <pod-name> -- sh
```

> Replace `<pod-name>` with your actual pod name.

---

### Can one pod ping or curl another pod?

**Yes!** Pods in the same Kubernetes cluster can freely communicate with each other.

#### How Pod Communication Works:

- Each pod gets a **unique IP** (e.g., `10.244.0.x`)
- Any pod can talk to any other pod directly via its IP.
- No need for:
    - Port forwarding
    - NodePort
    - Ingress (between pods)
- Kubernetes provides built-in **DNS-based service discovery**.

#### Option 1: Use Pod IP

```bash
kubectl get pods -o wide
curl http://10.244.0.54:5000
```

#### Option 2 (Recommended): Use Kubernetes Service Name

```bash
curl http://spring-boot-app:5000
```

> This works because Kubernetes sets up **DNS entries** for services.

- Same namespace: use `spring-boot-app`
- Different namespace: use
  ```
  spring-boot-app.<namespace>.svc.cluster.local
  ```

---

### What does `kubectl run` do?

- Creates a **single pod**
- Great for **on-the-fly debugging**
- Acts like a Kubernetes-native alternative to `docker run`

```bash
kubectl run -it --rm debug --image=busybox -- sh
```

---

## 1. YAML for a Standalone Pod (No Deployment)

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: easy-recipes-pod
spec:
  containers:
    - name: easy-recipes-container
      image: ram1uj/easy-recipes
      ports:
        - containerPort: 80
```

### Apply with:

```bash
kubectl apply -f easy-recipes-pod.yaml
```

> Runs a single pod with **no auto-restart** or **scaling**.

---

## 2. Deployment YAML (Recommended for Production)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: easy-recipes
spec:
  replicas: 2
  selector:
    matchLabels:
      app: easy-recipes
  template:
    metadata:
      labels:
        app: easy-recipes
    spec:
      containers:
        - name: easy-recipes
          image: ram1uj/easy-recipes
          ports:
            - containerPort: 80
```

### Apply with:

```bash
kubectl apply -f easy-recipes-deployment.yaml
```

This gives you:

- Multiple replicas
- Auto-recovery on crash
- Rolling updates

---

## Service YAML Example

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: myapp
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: NodePort
```

> This exposes your pod to the outside world using a **NodePort**.
