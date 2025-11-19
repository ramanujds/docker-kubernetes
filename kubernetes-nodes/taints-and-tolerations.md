### Example 1: Basic NoSchedule Example

#### Step 1 — Apply a Taint to a Node

You can taint a node so that only certain pods can run on it.  
For instance, if you want a node to only accept front‑end pods:

```bash
kubectl taint nodes node1 app=frontend:NoSchedule
```

- `node1` is the node’s name.
- `app` is the key, `frontend` is the value.
- `NoSchedule` means pods without a matching toleration will **not** be scheduled on this node.[1][3][5]

#### Step 2 — Pod Without Toleration

If you create a normal pod (without tolerations):

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: backend-pod
spec:
  containers:
  - name: nginx
    image: nginx
```

It will **not** be scheduled on that tainted node because it doesn’t have a matching toleration.[5]

#### Step 3 — Pod With Matching Toleration

Add a toleration to allow the pod to run there:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: frontend-pod
spec:
  containers:
  - name: nginx
    image: nginx
  tolerations:
  - key: "app"
    operator: "Equal"
    value: "frontend"
    effect: "NoSchedule"
```

Now the scheduler allows this pod to run on the tainted node.[2][3][1]

***

### Example 2: NoExecute Taint (Eviction Example)

`NoExecute` taints both prevent scheduling of new pods **and** evict existing pods unless they have a matching toleration.

#### Apply a Taint

```bash
kubectl taint nodes node2 app=blue:NoExecute
```

This taint tells Kubernetes to **immediately evict** all pods that don’t tolerate `app=blue`, and disallow new ones.[5]

#### Pod with Matching Toleration

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: blue-pod
spec:
  containers:
  - name: redis
    image: redis
  tolerations:
  - key: "app"
    operator: "Equal"
    value: "blue"
    effect: "NoExecute"
```

This pod can stay on `node2` and won’t be evicted, while pods without this toleration will be removed.[5]

***

### Example 3: Multiple Taints on the Same Node

Nodes can have multiple taints, and pods can tolerate some but not all of them.

#### Setting Multiple Taints

```bash
kubectl taint nodes minikube-m02 gpu=true:NoSchedule
kubectl taint nodes minikube-m02 project=system:NoExecute
kubectl taint nodes minikube-m02 type=process:NoSchedule
```

#### Pod with Partial Matching Tolerations

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-test
spec:
  containers:
  - name: nginx
    image: nginx:latest
  tolerations:
  - key: "gpu"
    operator: "Equal"
    value: "true"
    effect: "NoSchedule"
  - key: "project"
    operator: "Equal"
    value: "system"
    effect: "NoExecute"
```

Here, two taints match the pod’s tolerations; the third (`type=process`) doesn’t.  
Because one taint is not tolerated, the scheduler will avoid this node (due to the `NoSchedule` effect).[2]

***

### Example 4: PreferNoSchedule Taint (Soft Constraint)

If you want a node to avoid certain pods but not strictly block them, use:

```bash
kubectl taint nodes node3 workload=critical:PreferNoSchedule
```

Pods without a matching toleration **may** still be placed there if no better nodes exist.  
This is useful for preference rules rather than strict restrictions.[5]

***

### Example 5: Automatic Master Node Taints

By default, Kubernetes applies a taint on control plane nodes to stop user pods from running there:

```bash
kubectl describe node <master-node-name> | grep Taints
```

You’ll usually see something like:

```
node-role.kubernetes.io/master:NoSchedule
```

To allow a pod to run on the master node, add its toleration:

```yaml
tolerations:
- key: "node-role.kubernetes.io/master"
  effect: "NoSchedule"
```

This lets debugging or monitoring pods run on master nodes when needed.[3][5]

***

### Key Takeaway

- **Taints** mark nodes as restricted.
- **Tolerations** give pods permission to overcome those restrictions.
- They’re often used for **dedicated workloads, hardware segregation, maintenance,** or **fault isolation**.
- Taints alone do not guarantee where pods land — combine them with **node labels and affinities** for stricter control.[1][3][2][5]

### References

- Kubernetes Official Documentation on Taints and Tolerations
(https://kubernetes.io/docs/concepts/scheduling-eviction/taint-and-toleration/)
