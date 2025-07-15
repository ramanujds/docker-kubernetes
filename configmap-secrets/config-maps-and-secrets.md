# ConfigMaps and Secrets in Kubernetes

## ConfigMaps and Secrets are essential for managing configuration and sensitive data in Kubernetes applications.

### **1. Create a ConfigMap**
A **ConfigMap** is used to store non-sensitive configuration data such as `DB_HOST` and `DB_NAME`.

Create a file called `configmap.yml`:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: notes-service-config
data:
  DB_HOST: mysql
  DB_NAME: notes_db
```

Apply it using:
```sh
kubectl apply -f configmap.yml
```

---

### **2. Create a Secret**
A **Secret** is used to store sensitive data like `DB_USER` and `DB_PASSWORD`.

Create a file called `secret.yml`:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: notes-service-secret
type: Opaque
data:
  DB_USER: cm9vdA==        # Base64-encoded value of "root"
  DB_PASSWORD: cGFzc3dvcmQ= # Base64-encoded value of "password"
```

> ⚠ **Important**:
> - To encode values in Base64, use:
    >   ```sh
>   echo -n 'root' | base64
>   echo -n 'password' | base64
>   ```
> - To decode:
    >   ```sh
>   echo 'cm9vdA==' | base64 --decode
>   ```

Apply it using:
```sh
kubectl apply -f secret.yml
```

---

### **3. Update the Deployment YAML**
Now, modify your **Deployment YAML** to reference the **ConfigMap** and **Secret** instead of hardcoded values.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notes-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: notes-service
  template:
    metadata:
      labels:
        app: notes-service
    spec:
      containers:
        - name: notes-service
          image: ram1uj/notes-app-notes-service
          env:
            - name: DB_HOST
              valueFrom:
                configMapKeyRef:
                  name: notes-service-config
                  key: DB_HOST

            - name: DB_NAME
              valueFrom:
                configMapKeyRef:
                  name: notes-service-config
                  key: DB_NAME

            - name: DB_USER
              valueFrom:
                secretKeyRef:
                  name: notes-service-secret
                  key: DB_USER

            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: notes-service-secret
                  key: DB_PASSWORD

          ports:
            - containerPort: 8100
```

Apply the updated deployment:
```sh
kubectl apply -f deployment.yml
```

---

### **How It Works**
1. **ConfigMap (`notes-service-config`)**
    - Stores `DB_HOST` and `DB_NAME` (non-sensitive data).
    - Referenced using `configMapKeyRef`.

2. **Secret (`notes-service-secret`)**
    - Stores `DB_USER` and `DB_PASSWORD` (sensitive data).
    - Referenced using `secretKeyRef` (values are base64-encoded).

---

### **Advantages**

**Security**: Secrets are not stored in plain text in the YAML file.  
**Separation of Concerns**: Configuration and sensitive data are managed independently.  
**Easy Updates**: Update ConfigMaps/Secrets without redeploying applications.
