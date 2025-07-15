### **Kubernetes Rolling Updates & Horizontal Scaling**

In Kubernetes, **Rolling Updates** and **Horizontal Scaling** are essential mechanisms for managing deployments with minimal downtime and optimal resource usage.

---

## **1. Rolling Updates in Kubernetes**
A **Rolling Update** allows you to gradually replace old pod instances with new ones **without downtime**. Kubernetes updates the deployment by terminating old pods and creating new ones in a controlled manner.

### **How Rolling Updates Work**
- Ensures that at least some instances of your application remain available during the update.
- Uses a **rollingUpdate** strategy in the deployment.
- Controls how many pods are updated at a time using:
  - **maxUnavailable**: The maximum number of pods that can be unavailable during the update.
  - **maxSurge**: The maximum number of extra pods that can be created during the update.

### **Rolling Update Example**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1  # At most, 1 pod can be unavailable during the update
      maxSurge: 1        # At most, 1 extra pod will be created during the update
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
        image: my-app:v2  # New version of the application
        ports:
        - containerPort: 8080
```

### **Checking Update Progress**
1. **Trigger an update:**
   ```sh
   kubectl apply -f deployment.yaml
   ```
2. **Monitor rollout status:**
   ```sh
   kubectl rollout status deployment my-app
   ```
3. **Check rollout history:**
   ```sh
   kubectl rollout history deployment my-app
   ```
4. **Rollback to a previous version (if needed):**
   ```sh
   kubectl rollout undo deployment my-app
   ```

---

## **2. Horizontal Scaling in Kubernetes**
Kubernetes **Horizontal Scaling** increases or decreases the number of pods **based on resource utilization**.

### **Two Types of Scaling**
1. **Manual Scaling** – Increase/decrease pod count manually.
2. **Automatic Scaling (HPA - Horizontal Pod Autoscaler)** – Adjust pod count dynamically based on CPU/memory.

### **a. Manual Scaling Example**
Manually scale pods up or down:
```sh
kubectl scale deployment my-app --replicas=5
```
This increases the number of running pods to **5**.

---

### **b. Automatic Scaling with HPA (Horizontal Pod Autoscaler)**
HPA scales the number of pods based on CPU/memory utilization.

#### **HPA Example: Scale Based on CPU Usage**
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
          averageUtilization: 70  # Scale up when CPU usage exceeds 70%
```

#### **Apply and Check HPA**
1. **Deploy HPA**
   ```sh
   kubectl apply -f hpa.yaml
   ```
2. **Check HPA status**
   ```sh
   kubectl get hpa
   ```
3. **Generate load to trigger scaling**
   ```sh
   kubectl run -it --rm load-generator --image=busybox -- /bin/sh
   while true; do wget -q -O- http://my-app-service.default.svc.cluster.local; done
   ```
4. **Observe increased pod count**
   ```sh
   kubectl get pods
   ```

---

### **Best Practices for Rolling Updates & Scaling**
✅ **Use Probes**: Add **liveness** and **readiness** probes to avoid downtime during updates.
```yaml
livenessProbe:
  httpGet:
    path: /health
    port: 8080
  initialDelaySeconds: 5
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /ready
    port: 8080
  initialDelaySeconds: 5
  periodSeconds: 10
```

**Use Cluster Autoscaler**: If using HPA, ensure that **Cluster Autoscaler** is enabled to scale worker nodes.

**Set Resource Requests & Limits**: HPA works best when proper resource requests/limits are set.
```yaml
resources:
  requests:
    cpu: "250m"
    memory: "512Mi"
  limits:
    cpu: "500m"
    memory: "1Gi"
```

---

