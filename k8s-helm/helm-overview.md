
## **What is Helm?**
**Helm** is a **package manager for Kubernetes**, similar to how `apt` works for Ubuntu or `yum` for CentOS. It allows you to manage Kubernetes applications using **Helm Charts**, which are reusable, versioned, and configurable templates.

---

## **Goal**
We will create a **Helm Chart** to deploy the `ram1uj/easy-recipes` containerized application (which runs on **port 8080**) into a Kubernetes cluster.

---

## **Create a Helm Chart**
First, install Helm if you haven’t already:
```bash
curl -fsSL https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```
Now, create a new **Helm Chart**:
```bash
helm create easy-recipes-chart
```
This will generate the following structure:
```
easy-recipes-chart/
│── charts/
│── templates/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   ├── _helpers.tpl
│   ├── NOTES.txt
│── values.yaml
│── Chart.yaml
│── .helmignore
```
---

## **Define Application Configuration in `values.yaml`**
Open `values.yaml` and update the image, container port, and replica count.

```yaml
replicaCount: 2

image:
  repository: ram1uj/easy-recipes
  pullPolicy: IfNotPresent
  tag: "latest"
  containerPort: 8080

service:
  type: ClusterIP
  port: 80
  targetPort: 8080

ingress:
  enabled: false

resources: {}

nodeSelector: {}

tolerations: []

affinity: []
```
This sets:
- **Replica count** = 2 Pods
- **Container image** = `ram1uj/easy-recipes`
- **Service type** = `ClusterIP` (internal service)

---

## **Define Deployment in `templates/deployment.yaml`**
Modify `templates/deployment.yaml` to use the values from `values.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Release.Name }}-deployment
spec:
  replicas: {{ .Values.replicaCount }}
  selector:
    matchLabels:
      app: {{ .Release.Name }}
  template:
    metadata:
      labels:
        app: {{ .Release.Name }}
    spec:
      containers:
      - name: {{ .Chart.Name }}
        image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
        ports:
        - containerPort: {{ .Values.image.containerPort }}
```
This creates a **Deployment** with:
- **2 replicas**
- **Image pulled dynamically from `values.yaml`**
- **Port 80 exposed inside the container**

---

## **Define a Service in `templates/service.yaml`**
Modify `templates/service.yaml` to expose the Pods:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: {{ .Release.Name }}-service
spec:
  type: {{ .Values.service.type }}
  selector:
    app: {{ .Release.Name }}
  ports:
  - protocol: TCP
    port: {{ .Values.service.port }}
    targetPort: {{ .Values.service.targetPort }}
```
This creates a **Service** that exposes the Pods.

---

## **Deploy the Helm Chart**
### **Install the Chart in Kubernetes**
```bash
helm install easy-recipes easy-recipes-chart
```
Output:
```
NAME: easy-recipes
LAST DEPLOYED: Mon Feb 25 15:00:00 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
```

### **Verify Deployment**
```bash
kubectl get pods
kubectl get svc
```

---

## **Upgrade and Rollback**
### **Update the Chart**
If you modify `values.yaml`, apply changes using:
```bash
helm upgrade easy-recipes easy-recipes-chart
```

### **Rollback to the previous version**
```bash
helm rollback easy-recipes 1
```

---

## **Uninstall the Chart**
```bash
helm uninstall easy-recipes
```

---

