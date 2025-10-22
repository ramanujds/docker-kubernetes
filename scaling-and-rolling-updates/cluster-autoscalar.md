## What is a Cluster Autoscaler?

**Cluster Autoscaler** is a Kubernetes component that automatically **adds or removes nodes** in a cluster based on pod scheduling needs.

* **It scales the number of nodes** (in contrast to Horizontal Pod Autoscaler which scales pods).
* Helps when you run out of resources for pending pods.
* Saves cost by removing underutilized nodes.

---

## AWS EKS vs GKE: Built-in Autoscaler

| Platform | Built-in Autoscaler         | Manual Setup Needed?                 |
| -------- | --------------------------- | ------------------------------------ |
| **GKE**  | Yes (Native support)        | Not required (Just enable it)        |
| **EKS**  | No (Not enabled by default) | Yes, you must install & configure it |

---

## How to Use It on GKE (Google Kubernetes Engine)

**In GKE**, you simply enable autoscaling when creating a node pool.

### Step-by-step (Console):

1. Go to **Kubernetes Engine → Clusters**.
2. Click on your **cluster name**.
3. Click **Edit** for the node pool.
4. Enable **Autoscaling**.
5. Set min/max node count (e.g., 1–5).
6. Save.

That’s it! GKE manages it from there.

---

## How to Use It on EKS (AWS Kubernetes)

You need to **install Cluster Autoscaler manually** on EKS. Here's how:

### Step 1: Enable IAM permissions

Attach the required IAM policy to your node group role. Example:

```bash
aws iam attach-role-policy \
  --policy-arn arn:aws:iam::aws:policy/AutoScalingFullAccess \
  --role-name <NodeInstanceRole>
```

### Step 2: Deploy Cluster Autoscaler

You can deploy it with Helm or use the official YAML.

Here’s the **official YAML method**:

```bash
kubectl apply -f https://github.com/kubernetes/autoscaler/releases/download/cluster-autoscaler-<version>/cluster-autoscaler-autodiscover.yaml
```

> Replace `<version>` with your Kubernetes version (e.g. `v1.27.0`)

### Step 3: Modify the deployment

Edit the `cluster-autoscaler` deployment:

```bash
kubectl -n kube-system edit deployment cluster-autoscaler
```

And in the `args`, specify:

```yaml
- --cloud-provider=aws
- --nodes=1:5:<your-node-group-name>
- --balance-similar-node-groups
- --skip-nodes-with-system-pods=false
- --skip-nodes-with-local-storage=false
```

Also, set:

```yaml
containers:
  - name: cluster-autoscaler
    image: k8s.gcr.io/autoscaling/cluster-autoscaler:<version>
    ...
    env:
      - name: AWS_REGION
        value: us-east-1
```

---

## Cluster Autoscaler Works With:

* **HPA (Horizontal Pod Autoscaler)**: You scale pods and when new pods can't be scheduled due to resource limits, Cluster Autoscaler adds nodes.
* **VPA (Vertical Pod Autoscaler)**: Adjusts pod resources, which may trigger node scaling.

---

## How to Test

1. Deploy a pod with more resources than your node can handle.
2. You’ll see it pending.
3. Cluster Autoscaler will detect the unschedulable pod and **add a new node**.
4. When nodes are underutilized, it **removes them** automatically.

---

## Tip: Labels Matter

Cluster Autoscaler needs node groups or pools to be properly labeled:

For GKE:

```yaml
--node-group-autoprovisioning
```

For EKS:
Ensure the node group name is referenced in the autoscaler args.


