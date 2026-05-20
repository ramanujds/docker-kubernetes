# Kubernetes Hands-on Activity
## Scenario

You are a DevOps Engineer responsible for deploying an application on Kubernetes.

Your goal is to:

* Create a Deployment
* Expose it using a Service
* Apply Node Affinity
* Configure Horizontal Pod Autoscaling (HPA)
* Test autoscaling under load


---




# Step 1 — Create Namespace

## Task

Create a namespace named:

```bash
workshop
```


# Step 2 — Explore Nodes

## Task

List all cluster nodes.

```bash
kubectl get nodes
```

---

# Step 3 — Add Node Label

## Objective

We want application pods to run only on specific nodes.

## Task

Add a label to one node:

Key:

```text
workload
```

Value:

```text
frontend
```


---

# Step 4 — Create Deployment YAML


### Requirements

* Deployment name:

  ```text
  frontend-app
  ```

* Namespace:

  ```text
  workshop
  ```

* Replica count:

  ```text
  2
  ```

* Use nginx image

* Container port:

  ```text
  80
  ```

* Labels:

  ```text
  app=frontend
  ```

---

## Add Resource Requests & Limits

### CPU

* Request:

  ```text
  100m
  ```

* Limit:

  ```text
  300m
  ```

### Memory

* Request:

  ```text
  128Mi
  ```

* Limit:

  ```text
  256Mi
  ```

---

## Add Node Affinity

### Requirement

Pods should schedule only on nodes having:

```text
workload=frontend
```

Use:

```text
requiredDuringSchedulingIgnoredDuringExecution
```

---

## Tasks


* Write deployment YAML
* Apply manifest
* Verify deployment

---

# Step 5 — Verify Deployment

## Tasks

Check:

### Deployments

```bash
kubectl get deployment -n workshop
```

### Pods

```bash
kubectl get pods -n workshop
```

### Pod Placement

```bash
kubectl get pods -o wide -n workshop
```

---

# Step 6 — Create Service YAML

## Objective

Expose the application internally.

---

## Requirements

Create a Service with:

### Service Name

```text
frontend-service
```

### Namespace

```text
workshop
```

### Type

```text
ClusterIP
```

### Port

Expose:

```text
80
```

Target Port:

```text
80
```

### Selector

Match pods using:

```text
app=frontend
```

---


# Step 7 — Verify Service Connectivity

## Task

Create a temporary pod using BusyBox.

Inside the pod:

* Access service using service name
* Verify NGINX response

---

## Hint

Commands you may need:

```bash
kubectl run
```

```bash
wget
```

---

# Step 8 — Create HPA YAML

## Objective

Automatically scale pods based on CPU utilization.

---

## Requirements

Create an HPA with:

### HPA Name

```text
frontend-hpa
```

### Target Deployment

```text
frontend-app
```

### Min Replicas

```text
2
```

### Max Replicas

```text
10
```

### Scaling Metric

CPU Utilization

### Target CPU Usage

```text
50%
```

Use:

```text
autoscaling/v2
```

---

## Tasks


* Write HPA YAML
* Apply manifest
* Verify autoscaler

---

# Step 9 — Verify HPA

## Commands

```bash
kubectl get hpa -n workshop
```

```bash
kubectl describe hpa -n workshop
```

---


# Additional Challenges

## Challenge 1 — Convert ClusterIP to NodePort

Expose application externally.

---

## Challenge 2 — Add Readiness Probe

Participants should configure readiness checks.

---

## Challenge 3 — Add Liveness Probe

Restart unhealthy containers automatically.

---

## Challenge 4 — Use Preferred Node Affinity

Instead of mandatory affinity:

```text
preferredDuringSchedulingIgnoredDuringExecution
```

---

