## What is Ingress in Kubernetes?

**Ingress** is a **Kubernetes resource** that manages **external access** to your cluster’s services, typically over **HTTP/HTTPS**.

It acts like a **smart router or reverse proxy** inside your cluster.

---

## Why Not Just Use NodePort or LoadBalancer?

| Method        | Drawback |
|---------------|----------|
| `NodePort`    | Exposes service on a random high port of every node. Not scalable or friendly for users. |
| `LoadBalancer` | Each service gets its own external IP (expensive and cluttered). |

**Ingress solves this by:**
- Letting you route **multiple paths/domains through a single IP**
- Giving you **TLS/HTTPS support**
- Making it easier to manage **routing rules in one place**

---

## When to Use Ingress

Use **Ingress** when:
- You want to expose **multiple services** through **one IP/domain**
- You want **pretty URLs**, like:
    - `example.com/api`
    - `example.com/app`
- You need **TLS/HTTPS termination**
- You want **centralized traffic routing logic**

---

## Ingress Architecture

```
Internet
   |
Ingress Controller (e.g., NGINX, GKE Ingress)
   |
   |-- /app   --> app-service
   |-- /api   --> api-service
```

---

## How to Set Up Ingress (Basic Steps)

1. **Install an Ingress Controller** (NGINX, GKE Ingress, etc.)
    - Example for Minikube:
      ```bash
      minikube addons enable ingress
      ```

2. **Create Services** for your apps:
   ```bash
   kubectl expose deployment my-app --port=80 --target-port=8080
   ```

3. **Define Ingress resource YAML**:
   ```yaml
   apiVersion: networking.k8s.io/v1
   kind: Ingress
   metadata:
     name: my-ingress
     annotations:
       nginx.ingress.kubernetes.io/rewrite-target: /
   spec:
     rules:
     - host: myapp.local
       http:
         paths:
         - path: /
           pathType: Prefix
           backend:
             service:
               name: my-app
               port:
                 number: 80
   ```

4. **Access via `/etc/hosts` (for Minikube)**:
   ```text
   192.168.49.2 myapp.local
   ```

5. **Test in browser or curl**:
   ```bash
   curl http://myapp.local
   ```

---

## Popular Ingress Controllers
- **NGINX Ingress Controller** (most common)
- **GKE Ingress Controller** (if you're on GKE)
- **Traefik**, **Istio Gateway**, etc.

