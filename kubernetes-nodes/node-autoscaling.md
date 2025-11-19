### 1. What Node Autoscaling Does

When your workloads exceed the cluster’s current capacity, some pods will enter a **Pending** state. The **Cluster Autoscaler** or **Karpenter** watches for this and increases the node count automatically.  
Similarly, when resources are underused for a certain time (default 10 minutes), the scaler removes unneeded nodes to save cost.[1][2]

***

### 2. Using the Cluster Autoscaler (CA)

The **Cluster Autoscaler** scales **EKS Managed Node Groups** (Auto Scaling Groups behind the scenes). It reads Kubernetes’ scheduler information, identifies pending pods, and resizes node groups accordingly.[3][4][1]

#### Step 1: Prerequisites

Make sure you have:
- A running **EKS cluster**
- Installed CLI tools: `aws`, `kubectl`, and `eksctl`
- Correct IAM permissions to create policies and service accounts
- AWS OIDC provider configured for EKS (for IRSA authentication)[4][1]

#### Step 2: Tag Node Groups

Cluster Autoscaler discovers node groups using tags. For each Auto Scaling Group (ASG), add:

```
k8s.io/cluster-autoscaler/enabled
k8s.io/cluster-autoscaler/<cluster-name>
```

You can add these using the AWS Console or CLI:

```bash
aws autoscaling create-or-update-tags --tags Key=k8s.io/cluster-autoscaler/enabled,Value=true,PropagateAtLaunch=true
aws autoscaling create-or-update-tags --tags Key=k8s.io/cluster-autoscaler/my-eks-cluster,Value=true,PropagateAtLaunch=true
```

These tags tell AWS which node groups the autoscaler can modify.[1]

#### Step 3: Deploy the Cluster Autoscaler

Create a deployment using the official Cluster Autoscaler image:

```bash
kubectl apply -f https://github.com/kubernetes/autoscaler/releases/download/cluster-autoscaler-chart/cluster-autoscaler-autodiscover.yaml
```

You can also create a manifest manually:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: cluster-autoscaler
  namespace: kube-system
spec:
  replicas: 1
  selector:
    matchLabels:
      app: cluster-autoscaler
  template:
    metadata:
      labels:
        app: cluster-autoscaler
    spec:
      serviceAccountName: cluster-autoscaler
      containers:
        - name: cluster-autoscaler
          image: registry.k8s.io/autoscaling/cluster-autoscaler:v1.28.0
          command:
            - ./cluster-autoscaler
            - --cloud-provider=aws
            - --node-group-auto-discovery=asg:tag=k8s.io/cluster-autoscaler/enabled,k8s.io/cluster-autoscaler/my-eks-cluster
            - --balance-similar-node-groups=true
            - --skip-nodes-with-system-pods=false
          env:
            - name: AWS_REGION
              value: us-east-1
          resources:
            limits:
              cpu: 100m
              memory: 300Mi
```

This deployment watches your cluster size and adds or removes nodes through your ASGs transparently.[5][1]

***

### 3. Using Karpenter (Next‑Gen Autoscaler)

**Karpenter** is AWS’s newer and faster autoscaler designed for EKS. It launches EC2 instances directly, without needing ASGs, and right-sizes nodes based on pod demand. It is more cost-efficient than Cluster Autoscaler.[6][2]

#### Step 1: Install Karpenter

```bash
helm repo add karpenter https://charts.karpenter.sh/
helm repo update

helm install karpenter karpenter/karpenter \
  --namespace karpenter \
  --create-namespace \
  --set clusterName=my-eks-cluster \
  --set serviceAccount.create=true \
  --set aws.defaultInstanceProfile=KarpenterNodeInstanceProfile-my-eks-cluster \
  --set aws.clusterEndpoint="$(aws eks describe-cluster --name my-eks-cluster --query "cluster.endpoint" --output text)"
```

#### Step 2: Create a Karpenter Provisioner

Karpenter replaces static ASGs with flexible provisioning. Example:

```yaml
apiVersion: karpenter.sh/v1alpha5
kind: Provisioner
metadata:
  name: default
spec:
  limits:
    resources:
      cpu: 1000
  providerRef:
    name: default
  requirements:
    - key: "node.kubernetes.io/instance-type"
      operator: In
      values: ["t3.medium", "t3.large"]
  ttlSecondsAfterEmpty: 300
```

This allows Karpenter to launch nodes matching pod resource requirements dynamically and terminate them when idle.[6]

***

### 4. Choosing Between Cluster Autoscaler and Karpenter

| Feature | Cluster Autoscaler | Karpenter |
|----------|--------------------|------------|
| Scaling Target | AutoScaling Groups | Direct EC2 instances |
| Speed | Moderate | Very Fast |
| Cost Efficiency | Good | Excellent |
| Setup Complexity | Simple | Moderate |
| Best For | Existing ASGs or managed node groups | Dynamic, cost‑optimized workloads |

***

### 5. Additional Tips

- Combine **Cluster Autoscaler** (node scaling) with **Horizontal Pod Autoscaler (HPA)** for complete autoscaling (pods + nodes).
- Set your node group **minSize** and **maxSize** ranges carefully in EKS so the autoscaler knows its limits.
- Monitor autoscaling events via CloudWatch logs or `kubectl describe deployment cluster-autoscaler`.
- For spot instances, combine autoscaler with **Mixed Instance Policies** or use **Karpenter**, which handles spot interruptions gracefully.[2][1][6]

***

In short:  
On **EKS**, enable node autoscaling either by **deploying the Cluster Autoscaler** (works with EKS node groups) or **installing Karpenter** (for dynamic provisioning). Both automatically add nodes when workloads increase and remove idle ones to save cost.

### References

- https://devopscube.com/cluster-autoscaler/)
- https://www.stormit.cloud/blog/aws-eks-autoscaling/)
