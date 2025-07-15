

## **MySQL StatefulSet on Minikube**, including:

- Headless Service
- StatefulSet
- Persistent Volume Claim
- Secret for root password

This will give you a basic **MySQL StatefulSet setup on Minikube**, perfect for local dev/testing.

---

## Folder Structure

You can save all YAML in one file or separate like this:

```
mysql/
├── mysql-secret.yaml
├── mysql-service.yaml
├── mysql-statefulset.yaml
```

---

## 1. `mysql-secret.yaml` — Secret for Root Password
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mysql-root-pass
type: Opaque
data:
  password: bXlzcWwxMjM=  # base64 for "mysql123"
```

> You can generate your own password:
```bash
echo -n "yourpassword" | base64
```

---

## 2. `mysql-service.yaml` — Headless Service
```yaml
apiVersion: v1
kind: Service
metadata:
  name: mysql
spec:
  ports:
    - port: 3306
  clusterIP: None  # Important: Headless service
  selector:
    app: mysql
```

---

## 3. `mysql-statefulset.yaml` — StatefulSet
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql
spec:
  selector:
    matchLabels:
      app: mysql
  serviceName: "mysql"
  replicas: 1  # Can be scaled later
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
        - name: mysql
          image: mysql:5.7
          ports:
            - containerPort: 3306
              name: mysql
          env:
            - name: MYSQL_ROOT_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-root-pass
                  key: password
          volumeMounts:
            - name: mysql-persistent-storage
              mountPath: /var/lib/mysql
  volumeClaimTemplates:
    - metadata:
        name: mysql-persistent-storage
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 1Gi
```

---

## Apply the Setup on Minikube

Run these in order:
```bash
kubectl apply -f mysql-secret.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f mysql-statefulset.yaml
```

Check pods:
```bash
kubectl get pods
```

Check PVC:
```bash
kubectl get pvc
```

---

## Access MySQL from Inside the Cluster

Create a temporary pod to connect:
```bash
kubectl run -it mysql-client --image=mysql:5.7 --rm --restart=Never -- bash
```

Inside the shell:
```bash
mysql -h mysql-0.mysql -u root -p
# Enter password: mysql123
```

