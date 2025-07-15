### **Debugging & Dry Running a Helm Chart**

Before deploying a Helm chart, it’s a good practice to **debug** and **dry-run** it to identify any potential issues.

---

## **Helm Dry Run (`--dry-run`)**
The **dry-run** feature allows you to see the rendered Kubernetes manifests **without** actually applying them.

### **Dry Run a Helm Install**
```bash
helm install notes-app notes-app-chart --dry-run --debug
```
**What it does:**
- Renders the YAML files with values substituted
- Validates syntax and Helm templating
- Does NOT create any Kubernetes resources

---

## **Helm Debugging (`--debug`)**
The `--debug` flag provides additional output useful for debugging.

### **Debug a Helm Install**
```bash
helm install notes-app notes-app-chart --debug
```
**What it does:**
- Shows the full rendered manifests
- Highlights possible issues

---

## **Helm Template (`helm template`)**
The `helm template` command renders the Kubernetes manifests **without deploying** them.

### **Render Helm Templates Locally**
```bash
helm template notes-app notes-app-chart
```
**What it does:**
- Shows exactly how Kubernetes manifests will be applied
- Helps check if values are correctly replaced

---

## **Helm Lint (`helm lint`)**
The `helm lint` command checks for syntax errors in your Helm chart.

### **Lint Helm Chart**
```bash
helm lint notes-app-chart
```
**What it does:**
- Identifies formatting and structural issues
- Highlights missing fields in YAML files

---

## **Debugging a Deployed Chart**
If the Helm chart is already deployed but something isn't working, check the pods, logs, and Helm release details.

### **Check Helm Release**
```bash
helm list
helm status notes-app
```
**What it does:**
- Lists all installed Helm releases
- Shows the status of the `notes-app` release

### **Check Kubernetes Pods**
```bash
kubectl get pods
kubectl describe pod <pod-name>
```
**What it does:**
- Checks if pods are running or crashing
- Provides detailed logs and errors

### **🛠️ Check Kubernetes Logs**
```bash
kubectl logs -l app=user-service
```
**What it does:**
- Fetches logs for a specific service

---

## **Quick Debugging Workflow**
**Lint the chart**
```bash
helm lint notes-app-chart
```
**Render templates without deploying**
```bash
helm template notes-app notes-app-chart
```
3**Run a dry-run install**
```bash
helm install notes-app notes-app-chart --dry-run --debug
```
**Deploy and check the status**
```bash
helm install notes-app notes-app-chart
helm status notes-app
kubectl get pods
```
**Check logs for issues**
```bash
kubectl logs -l app=user-service
```
