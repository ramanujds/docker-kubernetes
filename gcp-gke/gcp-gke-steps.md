
### How to **create and use a GKE cluster from Google Cloud Shell** step-by-step:

---

## Step-by-Step: Use GKE from Google Cloud Shell

### 1. **Open Google Cloud Shell**
- Visit: [https://console.cloud.google.com](https://console.cloud.google.com)
- Click the **Cloud Shell icon** (terminal button in the top right)

---

### 2. **Verify Your Project & Set Zone**
```bash
gcloud config list
gcloud config set compute/zone us-central1-a
```

Check available zones:
```bash
gcloud compute zones list
```

---

### 3. **Enable GKE API**
```bash
gcloud services enable container.googleapis.com
```

---

### 4. **Create a GKE Cluster**
```bash
gcloud container clusters create demo-cluster --num-nodes=2
```

Optional: add machine type or auto-scaling flags:
```bash
gcloud container clusters create demo-cluster \
  --num-nodes=2 \
  --machine-type=e2-medium \
  --enable-autoscaling --min-nodes=1 --max-nodes=3
```

---

### 5. **Configure `kubectl` for GKE**
```bash
gcloud container clusters get-credentials demo-cluster
```

This sets your current `kubectl` context to the new GKE cluster.

---

### 6. **Verify Cluster is Working**
```bash
kubectl get nodes
```

---

### 7. **Deploy an Application**
```bash
kubectl create deployment myapp --image=nginx
kubectl expose deployment myapp --type=LoadBalancer --port=80
```

---

### 8. **Access the Application**
```bash
kubectl get svc
```

Wait for the `EXTERNAL-IP` to show up, then open it in your browser.

---

## 🧹 Cleanup
To avoid billing charges:
```bash
gcloud container clusters delete demo-cluster
```

---
