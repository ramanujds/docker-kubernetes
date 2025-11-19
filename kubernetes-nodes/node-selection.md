In Kubernetes, if you have two nodes and want to schedule specific pods onto a particular node based on labels or certain properties, you can achieve that using **node labels** combined with either **nodeSelector** or **nodeAffinity**. Both are native scheduling mechanisms that let you control where your workload runs, but they differ in flexibility.

***

### Step 1: Label Your Nodes

First, list your nodes to identify their names:

```bash
kubectl get nodes --show-labels
```

Then, apply a label to the node you want to target (for example, label one node as `disktype=ssd`):

```bash
kubectl label nodes node1 disktype=ssd
kubectl label nodes node2 disktype=hdd
```

You can verify the labels applied with:

```bash
kubectl get nodes --show-labels
```

This assigns descriptive metadata to each node that can be referenced during scheduling.[1][2][3]

***

### Step 2: Use **nodeSelector** (Simple Approach)

The easiest way to schedule pods to a specific node is via **nodeSelector**. It matches key-value pairs of node labels.

Example Pod YAML:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-ssd
spec:
  containers:
    - name: nginx
      image: nginx
  nodeSelector:
    disktype: ssd
```

This configuration ensures that the pod runs **only on nodes labeled `disktype=ssd`**.[4][5][1]

However, note that nodeSelector performs a strict match — if no node has that label, the pod will remain unscheduled.[5]

***

### Step 3: Use **nodeAffinity** (Recommended Approach)

Node affinity provides more control and flexibility than nodeSelector. It supports both **required** (hard rule) and **preferred** (soft rule) conditions.

Example YAML using node affinity:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-app
spec:
  containers:
    - name: nginx
      image: nginx:latest
  affinity:
    nodeAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
        nodeSelectorTerms:
        - matchExpressions:
          - key: disktype
            operator: In
            values:
            - ssd
```

This pod will only be scheduled on nodes that have the label `disktype=ssd`. The `operator: In` ensures that any node with that label value qualifies.[3][6][7]

***

### Step 4: Combining with Pod Labels (Optional)

If your pod has specific labels (like `app=tier1`), you can combine label selectors with node affinity to target both the **pod type** and the **node property** together. For example, only `app=tier1` pods go to `disktype=ssd` nodes.

In a deployment YAML:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tier1-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: tier1
  template:
    metadata:
      labels:
        app: tier1
    spec:
      affinity:
        nodeAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            nodeSelectorTerms:
            - matchExpressions:
              - key: disktype
                operator: In
                values:
                - ssd
      containers:
      - name: web
        image: nginx
```

Now any `tier1` pod from this deployment will be placed only on nodes with the `disktype=ssd` label.[6][7]

***

### Summary Table

| Method           | Description | Flexibility | Example Use |
|------------------|-------------|--------------|--------------|
| nodeSelector     | Direct label match | Simple but rigid | Perfect for small clusters with fixed rules [5] |
| nodeAffinity     | Advanced matching with operators | Highly flexible | Ideal for dynamic environments with complex scheduling needs [3][6] |
***



