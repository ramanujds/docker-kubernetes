# **Step-by-Step Kubernetes Deployment Using Commands Only**

We will deploy the `ram1uj/easy-recipes` application in Kubernetes **without YAML files**, using only `kubectl` commands.

---

## **Steps to Deploy the Application**
1. **Start Kubernetes Cluster (if not running)**
2. **Create a Deployment**
3. **Expose the Deployment as a Service**
4. **Verify Deployment and Access the Application**

---

## **Step 1: Start Kubernetes Cluster**
If you don’t have a running cluster, start **Minikube** (for local testing):
```bash
minikube start
```
Verify the cluster:
```bash
kubectl cluster-info
```

---

## **Step 2: Deploy the Application**
Run the following command to create a **Deployment** with 2 replicas:
```bash
kubectl create deployment easy-recipes --image=ram1uj/easy-recipes --port=80 --replicas=2
```
This creates a **Deployment** named `easy-recipes` running `ram1uj/easy-recipes`.

---

## **Step 3: Expose the Deployment as a Service**
Now, expose the deployment so it is accessible:
```bash
kubectl expose deployment easy-recipes --type=NodePort --port=80 --target-port=8080
```
This creates a Service named `easy-recipes` that maps port 80 to the application running on port 8080.

---

## **Step 4: Verify Deployment and Access the App**
### **Check if the Pods are Running**
```bash
kubectl get pods
```
You should see 2 running pods.

---

### **Check the Service Details**
```bash
kubectl get svc easy-recipes
```
- If running in **cloud**, note the **External IP**.
- If using **Minikube**, get the URL:
  ```bash
  minikube service easy-recipes --url
  ```

---

### **Access the Application**
- Open in your browser:
  ```
  http://<EXTERNAL-IP>:80
  ```
- Or, use **Minikube** to open it automatically:
  ```bash
  minikube service easy-recipes
  ```

---

## **Cleanup (Optional)**
To delete the deployment and service:
```bash
kubectl delete deployment easy-recipes
kubectl delete service easy-recipes
```

---
