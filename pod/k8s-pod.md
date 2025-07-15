

## **How to Access a Pod Using Its IP Address?**
Each Pod in Kubernetes gets an **internal IP** within the cluster. You can find the IP using:

```bash
kubectl get pods -o wide
```
Example output:
```
NAME           READY   STATUS    IP           NODE
easy-recipes   1/1     Running   10.244.1.12  worker-node-1
```
Here, the Pod's IP is **`10.244.1.12`**.

To access it:
```bash
curl http://10.244.1.12:80
```

This works **only within the cluster** because Pod IPs are assigned by Kubernetes' internal network.


## **Limitations of Accessing Pods via IP**
1. **Pods are ephemeral** – If a Pod is restarted or rescheduled, its IP changes.
2. **Pod IPs are not accessible externally** – You can’t access Pod IPs from outside the cluster.
3. **Networking policies may block access** – Some clusters restrict direct Pod-to-Pod communication.



## **Better Ways to Access Pods**
### **Use a Service (Recommended)**
Instead of using the Pod’s IP, expose it using a **Service**:
```bash
kubectl expose pod easy-recipes --type=ClusterIP --port=80 --name=easy-recipes-service
```
Then, access it within the cluster:
```bash
curl http://easy-recipes-service:80
```

---

### **Use Port Forwarding (For Debugging)**
If you need direct access to a Pod from your **local machine**:

```bash
kubectl port-forward pod/easy-recipes 8080:80
```
Now, open:
```
http://localhost:8080
```

---

### **Use a NodePort or LoadBalancer Service**
If you need **external** access, expose the Pod using a **NodePort** or **LoadBalancer**:
```bash
kubectl expose pod easy-recipes --type=NodePort --port=80
kubectl get svc easy-recipes
```
Look for the `NodePort` and access:
```
http://<NODE-IP>:<NODE-PORT>
```



### **Can One Pod Access Another in Kubernetes?**
**Yes!** One Pod can communicate with another Pod in Kubernetes, but it depends on how networking is set up. 
Here’s how you can achieve inter-pod communication.

---

## **Accessing Another Pod Using Its IP (Not Recommended)**
Every Pod gets an **IP address** assigned by Kubernetes. You can find it with:

```bash
kubectl get pods -o wide
```
Example output:
```
NAME             READY   STATUS    IP           NODE
app-pod         1/1     Running   10.244.1.12  worker-node-1
db-pod          1/1     Running   10.244.1.15  worker-node-1
```
You can then `curl` the other Pod (inside the cluster):

```bash
kubectl exec -it app-pod -- curl http://10.244.1.15:80
```

**Limitations:**
- Pod IPs **change** when restarted.
- Not a **stable** way to communicate.
- Works **only inside the cluster**.

---

## ** Accessing Another Pod Using a Service (Recommended)**
A **Kubernetes Service** provides a **stable** way for Pods to communicate. Instead of using **Pod IPs**, we use the **Service name**.

### **Create a Deployment for an App (Example: Frontend)**
```bash
kubectl create deployment frontend --image=nginx
```

### **Create a Deployment for Another App (Example: Backend)**
```bash
kubectl create deployment backend --image=ram1uj/easy-recipes
```

### **Expose the Backend as a Service**
```bash
kubectl expose deployment backend --port=80 --name=backend-service
```
This creates a **ClusterIP Service**, allowing internal access.

### **Access Backend from Frontend Pod**
Now, inside the **frontend pod**, we can access **backend-service** by name:

```bash
kubectl exec -it $(kubectl get pods -l app=frontend -o jsonpath='{.items[0].metadata.name}') -- curl http://backend-service:80
```

**Pods can now communicate using the stable service name instead of changing Pod IPs.**

---

## **Using Environment Variables for Service Discovery**
When a Service is created, Kubernetes automatically adds environment variables in Pods that match the Service name.

Example:
```bash
kubectl exec -it frontend-pod -- env | grep BACKEND_SERVICE
```
You will see environment variables like:
```
BACKEND_SERVICE_SERVICE_HOST=10.96.0.10
BACKEND_SERVICE_SERVICE_PORT=80
```
You can use these variables in your application to connect to the backend.

---

## ** Using DNS for Pod-to-Pod Communication**
Kubernetes has an internal **DNS service** that allows Pods to communicate using **service names**.

If a Service is called **backend-service**, it is reachable at:
```
http://backend-service.default.svc.cluster.local
```
You can test this inside any Pod:
```bash
kubectl exec -it frontend-pod -- curl http://backend-service.default.svc.cluster.local
```


