# Kubernetes on EKS (Elastic Kubernetes Service)

## OPTION 1: Using `eksctl` (Recommended for Beginners)

### Prerequisites

* AWS CLI configured (`aws configure`)
* `eksctl` installed → [https://eksctl.io](https://eksctl.io)
* `kubectl` installed → [Kubernetes CLI](https://kubernetes.io/docs/tasks/tools/)

---

### Step-by-Step

#### 🔹 Step 1: Install `eksctl` (if not already)


#### 🔹 Step 2: Create an EKS Cluster

```bash
eksctl create cluster \
  --name my-cluster \
  --region us-east-2 \
  --nodegroup-name linux-nodes \
  --node-type t3.medium \
  --nodes 2 \
  --nodes-min 1 \
  --nodes-max 3 \
  --managed
```


#### With autoscaling:

```bash

eksctl create cluster \
  --name my-cluster --version 1.33 \
  --region us-east-2 --nodegroup-name my-workers \
  --node-type t3.medium --nodes 2 --nodes-min 1 --nodes-max 3 \
  --managed --asg-access


```

> It automatically sets up:
>
> * EKS control plane
> * EC2 worker nodes (managed node group)
> * VPC, subnets, IAM roles
> * Auth for `kubectl`

#### Step 3: Verify the Cluster

```bash
kubectl get nodes
kubectl get svc
```

You should see your nodes in `Ready` status.

---

## OPTION 2: Using AWS Console (UI)

> Best for those who prefer GUI

1. Go to AWS Console → EKS → “Create cluster”
2. Configure:

    * Cluster name
    * Kubernetes version
    * Role (create one if needed)
    * VPC/Subnet settings
3. Create a **Node Group** (managed)
4. Connect using `aws eks update-kubeconfig` command to `kubectl`

---

## OPTION 3: Using Terraform (Infrastructure as Code)

Create EKS using Terraform (more complex, good for production/CI/CD).

## What Gets Created?

| Resource                | Managed By                     |
| ----------------------- | ------------------------------ |
| EKS Control Plane       | AWS (serverless)               |
| EC2 Nodes               | Your AWS account               |
| VPC, subnets            | Default or custom              |
| IAM roles & policies    | Needed for EKS and nodes       |
| `kubectl` access config | Mapped in `aws-auth` ConfigMap |

---

## IAM Permissions Required

You (or your CLI user) should have:

* `eks:*`
* `ec2:*`
* `iam:*`
* `cloudformation:*`
* `autoscaling:*`

Or use the pre-built **`AdministratorAccess`** policy.

---

## Cleanup

```bash
eksctl delete cluster --name my-cluster --region us-east-2
```

