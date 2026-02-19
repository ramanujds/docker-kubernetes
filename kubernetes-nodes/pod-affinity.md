Now we’re getting into the **really interesting part of Kubernetes scheduling** — this is where microservices
architecture becomes intelligent.

If Node Affinity is:

> “Which node should I run on?”

Then **Pod Affinity / Anti-Affinity** is:

> “Which other pods should I run near… or away from?”

This is pod-to-pod relationship based scheduling.

And this is super relevant for distributed systems, microservices, databases, caching layers — basically everything you
teach.

---

# Big Idea

Pod Affinity / Anti-Affinity lets you control scheduling based on:

* Labels of other pods
* Topology (node, zone, region, etc.)

So instead of looking at node labels, it looks at **other pods’ labels**.

---

# Real-World Mental Model

Pod Affinity:

> “I want to run on the same node (or zone) as Pod X.”

Pod Anti-Affinity:

> “I don’t want to run on the same node as Pod X.”

---

# Important: Topology Key

This defines “what is considered close?”

Most common values:

```
kubernetes.io/hostname   → same node
topology.kubernetes.io/zone → same AZ
```

On your docker-desktop (single node), only hostname matters.

---

# Example 1 — Pod Affinity (Run Together)

Let’s simulate:

### Step 1: Deploy a backend pod

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
spec:
  replicas: 1
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
        - name: nginx
          image: nginx
```

Apply it.

---

### Step 2: Deploy frontend with Pod Affinity

Now we tell frontend:

> Run on same node as backend.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend
spec:
  replicas: 1
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
    spec:
      affinity:
        podAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            - labelSelector:
                matchExpressions:
                  - key: app
                    operator: In
                    values:
                      - backend
              topologyKey: kubernetes.io/hostname
      containers:
        - name: nginx
          image: nginx
```

Now:

Frontend will only schedule if backend exists on a node.

Since you have only one node, it works.

If backend is deleted → frontend stays Pending.

---

# Example 2 — Pod Anti-Affinity (Spread Apart)

This is extremely important in production.

Imagine 3 replicas of a microservice.

You don’t want all replicas on same node.

Use Anti-Affinity:

```yaml
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: app
              operator: In
              values:
                - backend
        topologyKey: kubernetes.io/hostname
```

If you scale:

```bash
kubectl scale deployment backend --replicas=3
```

On a real multi-node cluster:

Scheduler spreads them across nodes.

On docker-desktop (1 node):

Only one replica will run.
Others will stay Pending.

That’s expected behavior.

---

# Required vs Preferred (Same Concept Again)

You have:

### requiredDuringSchedulingIgnoredDuringExecution

Hard rule — must follow

### preferredDuringSchedulingIgnoredDuringExecution

Soft rule — try but not mandatory

In production, Anti-Affinity is often preferred (soft) to avoid scheduling deadlocks.

---

# Why Anti-Affinity Is CRITICAL in Microservices

Imagine:

* 3 replicas of payment-service
* All land on same node
* Node crashes

Boom. Entire service down.

Anti-Affinity ensures:

Each replica runs on different node.

High availability achieved.

---

# Real EKS Example (This Is Gold)

Multi-AZ cluster:

Nodes in:

* ap-south-1a
* ap-south-1b
* ap-south-1c

Use topology key:

```
topology.kubernetes.io/zone
```

Now replicas get distributed across AZs.

This is how you design HA architecture properly.

---

# Scheduler Logic (Simplified)

When Pod is created:

1. Check node taints
2. Check node affinity
3. Check pod affinity/anti-affinity
4. Score nodes
5. Pick best one

Pod affinity adds computational complexity — that’s why it’s expensive in very large clusters.

---

# Common Interview Question

Q: What happens if required anti-affinity can't be satisfied?

A:
Pod remains Pending.

Q: Why can anti-affinity cause scheduling deadlock?

A:
If replicas require separation but not enough nodes exist.

---

# Production Patterns You Should Teach

### Pattern 1 — Cache Near Backend

Use Pod Affinity:

* Redis close to backend

### Pattern 2 — Spread Replicas

Use Pod Anti-Affinity:

* Web replicas spread across nodes

### Pattern 3 — Multi-AZ HA

Anti-affinity + zone topology

---

# Difference Summary

| Type              | Works On    | Purpose               |
|-------------------|-------------|-----------------------|
| Node Affinity     | Node labels | Choose infrastructure |
| Pod Affinity      | Pod labels  | Run together          |
| Pod Anti-Affinity | Pod labels  | Spread apart          |
| Taints            | Node rules  | Block pods            |

---

# Very Important Limitation

Pod Affinity:

* More CPU expensive for scheduler
* Avoid complex rules in large clusters
* Prefer topology spread constraints (modern alternative)

If you want next level, we can go into:

* Topology Spread Constraints (modern HA way)
* How ReplicaSets + Anti-Affinity interact
* Real EKS architecture diagram explanation
* Scheduler scoring algorithm internally

You’re basically building full production-level scheduling knowledge now.
Tell me where you want to go deeper.
