# Kubernetes Deployment Best Practices

## **1. Application Configuration & Management**
### **a. Use ConfigMaps & Secrets**
- Store environment-specific configurations in **ConfigMaps** instead of hardcoding them.
- Store sensitive data (passwords, API keys) in **Secrets**, not in environment variables or plain YAML.
- Use **RBAC** to restrict access to Secrets.

### **b. Use Helm Charts or Kustomize**
- **Helm** helps manage deployments with versioned charts.
- **Kustomize** allows environment-specific customization without duplication.

---

## **2. Resource Optimization**
### **a. Set Resource Requests & Limits**
- Avoid resource starvation by defining `requests` and `limits` for **CPU** and **Memory**.
- Example:
  ```yaml
  resources:
    requests:
      memory: "256Mi"
      cpu: "250m"
    limits:
      memory: "512Mi"
      cpu: "500m"
  ```

### **b. Use Horizontal & Vertical Scaling**
- Enable **Horizontal Pod Autoscaler (HPA)** for scaling based on CPU/memory.
  ```yaml
  apiVersion: autoscaling/v2
  kind: HorizontalPodAutoscaler
  metadata:
    name: my-app-hpa
  spec:
    scaleTargetRef:
      apiVersion: apps/v1
      kind: Deployment
      name: my-app
    minReplicas: 2
    maxReplicas: 10
    metrics:
      - type: Resource
        resource:
          name: cpu
          target:
            type: Utilization
            averageUtilization: 70
  ```
- Use **Cluster Autoscaler** to dynamically adjust node count.

---

## **3. Deployment Strategy**
### **a. Use Rolling Updates**
- Ensure zero-downtime deployment by using rolling updates.
  ```yaml
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
  ```

### **b. Canary or Blue-Green Deployment**
- **Canary**: Gradually roll out a new version to a subset of users.
- **Blue-Green**: Keep two versions running and switch traffic when ready.

---

## **4. Networking Best Practices**
### **a. Use Ingress Controllers**
- Manage external traffic using **Ingress** (e.g., Nginx, Traefik).
- Example:
  ```yaml
  apiVersion: networking.k8s.io/v1
  kind: Ingress
  metadata:
    name: my-ingress
  spec:
    rules:
    - host: myapp.example.com
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: my-service
              port:
                number: 80
  ```

### **b. Implement Network Policies**
- Restrict pod-to-pod communication.
  ```yaml
  apiVersion: networking.k8s.io/v1
  kind: NetworkPolicy
  metadata:
    name: allow-app-to-db
  spec:
    podSelector:
      matchLabels:
        app: db
    ingress:
    - from:
      - podSelector:
          matchLabels:
            app: app
  ```

---

## **5. Security Best Practices**
### **a. Use Role-Based Access Control (RBAC)**
- Grant least privilege access using **RBAC**.
  ```yaml
  apiVersion: rbac.authorization.k8s.io/v1
  kind: Role
  metadata:
    namespace: default
    name: pod-reader
  rules:
  - apiGroups: [""]
    resources: ["pods"]
    verbs: ["get", "watch", "list"]
  ```

### **b. Enable Pod Security Policies (PSP) or Pod Security Admission**
- Avoid running containers as **root**.
  ```yaml
  securityContext:
    runAsUser: 1000
    runAsNonRoot: true
    readOnlyRootFilesystem: true
    capabilities:
      drop:
        - ALL
  ```

### **c. Use Image Scanning & Signing**
- Scan container images with tools like **Trivy** or **Clair**.
- Sign images with **Cosign** before deployment.

---

## **6. Observability & Monitoring**
### **a. Logging with Fluentd or EFK (Elasticsearch, Fluentd, Kibana)**
- Use centralized logging to track application logs.

### **b. Monitoring with Prometheus & Grafana**
- Set up **Prometheus** for metrics collection and **Grafana** for visualization.
- Example Prometheus ServiceMonitor:
  ```yaml
  apiVersion: monitoring.coreos.com/v1
  kind: ServiceMonitor
  metadata:
    name: my-app
  spec:
    selector:
      matchLabels:
        app: my-app
    endpoints:
    - port: http
      interval: 30s
  ```

### **c. Distributed Tracing with Jaeger or OpenTelemetry**
- Trace requests across microservices for performance tuning.

---

## **7. Storage & Data Persistence**
### **a. Use Persistent Volumes (PV) & Persistent Volume Claims (PVC)**
- Store data using **Persistent Volumes** instead of container filesystem.
  ```yaml
  apiVersion: v1
  kind: PersistentVolumeClaim
  metadata:
    name: my-pvc
  spec:
    accessModes:
      - ReadWriteOnce
    resources:
      requests:
        storage: 1Gi
  ```

### **b. Backup & Disaster Recovery**
- Use tools like **Velero** for backup and restore.

---

## **8. CI/CD & GitOps**
### **a. Implement CI/CD with ArgoCD or Flux**
- Use **ArgoCD** or **Flux** for GitOps-based deployments.
- Automate deployments using **Jenkins, GitHub Actions, or GitLab CI/CD**.

### **b. Image Tagging & Versioning**
- Avoid using `latest` tag. Instead, version images properly.

