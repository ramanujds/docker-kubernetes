# Overview of Kubernetes Deployment YAML Configuration

### **1. apiVersion: apps/v1**
- Specifies the API version of Kubernetes to be used for this configuration.
- `apps/v1` is the current stable API version for `Deployment` objects.

---

### **2. kind: Deployment**
- Defines the type of Kubernetes object being created.
- A `Deployment` is used to manage a set of identical Pods, ensuring high availability and scaling.

---

### **3. metadata**
Contains metadata (like name and labels) to uniquely identify the deployment.

#### **metadata:**
```yaml
metadata:
  name: notes-service
```
- `name: notes-service`
    - Assigns a unique name (`notes-service`) to this deployment.
    - This name can be used to reference the deployment in other Kubernetes configurations.

---

### **4. spec**
Defines the desired state of the deployment.

#### **4.1 replicas**
```yaml
replicas: 1
```
- Specifies the number of pod instances to be created and maintained.
- Here, `1` means only one replica of the pod will run.
- For high availability, you might set this to a higher number.

#### **4.2 selector**
```yaml
selector:
  matchLabels:
    app: notes-service
```
- Defines how Kubernetes identifies which pods belong to this deployment.
- The `matchLabels` key ensures that only pods with the label `app: notes-service` are managed by this deployment.

---

### **5. template**
Defines the pod specification that will be created by this deployment.

#### **5.1 metadata (within template)**
```yaml
metadata:
  labels:
    app: notes-service
```
- Assigns the label `app: notes-service` to the pod.
- This must match the selector label in the `spec.selector.matchLabels`.

#### **5.2 spec (within template)**
Defines the specifications for the containers running inside the pod.

---

### **6. Containers**
```yaml
containers:
  - name: notes-service
    image: ram1uj/notes-app-notes-service
```
- **`name: notes-service`**
    - The name of the container inside the pod.
    - Used for logging and referencing within the pod.

- **`image: ram1uj/notes-app-notes-service`**
    - Specifies the Docker image to be used for the container.
    - The image `ram1uj/notes-app-notes-service` is pulled from Docker Hub (or a private registry if configured).
    - The container is created using this image.

---

### **7. Environment Variables**
```yaml
env:
  - name: DB_HOST
    value: mysql
  - name: DB_NAME
    value: notes_db
  - name: DB_USER
    value: root
  - name: DB_PASSWORD
    value: password
```
- Specifies environment variables for the container.
- These variables can be used by the application inside the container.

- **`DB_HOST: mysql`**
    - The database host is set to `mysql`, meaning the application will connect to a MySQL service.

- **`DB_NAME: notes_db`**
    - Specifies the database name as `notes_db`.

- **`DB_USER: root`**
    - Sets the database username as `root`.

- **`DB_PASSWORD: password`**
    - Sets the database password as `password` (should be managed using Kubernetes Secrets in production).

---

### **8. Ports**
```yaml
ports:
  - containerPort: 8100
```
- Specifies the container's exposed port.
- `8100` is the port on which the application inside the container will listen.
- Other services or pods can communicate with this application on port `8100`.

---

### **9. Image Pull Policy**
```yaml
imagePullPolicy: Always
```
- Defines when Kubernetes should pull the image.
- `Always` means Kubernetes will always pull the latest image from the registry whenever the pod is created or restarted.
- This is useful during development but can be changed to `IfNotPresent` or `Never` in production for performance reasons.

---

### **Best Practices**
1. **Use ConfigMaps & Secrets**
    - Instead of hardcoding environment variables (like `DB_PASSWORD`), store them in a **ConfigMap** or **Secret**.

2. **Increase Replicas for High Availability**
    - If you want redundancy, set `replicas` to a higher value.

3. **Use Resource Limits**
    - You can specify resource limits (`resources.requests` and `resources.limits`) for better control over CPU/memory usage.

---
