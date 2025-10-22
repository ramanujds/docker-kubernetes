### What is Kustomize

Kustomize is a **Kubernetes-native configuration management tool** that lets you **customize resource YAML files** in a reusable, declarative way—**without modifying the original (base) files**. It was added natively into `kubectl` (via the `-k` option) starting in **Kubernetes v1.14**.[2][4][6]

Unlike Helm, which uses templates, Kustomize works solely with **plain YAML overlays**. It lets teams create a **base configuration** (shared across environments) and then apply **environment-specific patches** (for example, dev, staging, or production).[3][4]

***

### Core Concepts

1. **Base** – Common configuration shared by all environments (like Deployments, Services).
2. **Overlay** – Environment-specific customizations layered over the base using patching and variable substitution.
3. **kustomization.yaml** – The control file that defines which resources and patches are used together.
4. **Patches** – YAML fragments that modify specific fields (e.g., replica count, image tag) without editing base files.[4][6]

***

### Directory Structure Example

Here’s a simple structure for an app managed with Kustomize:

```
myapp/
 ├── base/
 │   ├── deployment.yaml
 │   ├── service.yaml
 │   └── kustomization.yaml
 └── overlays/
     ├── dev/
     │   ├── kustomization.yaml
     │   └── patch.yaml
     └── prod/
         ├── kustomization.yaml
         └── patch.yaml
```

***

### Example: Basic Usage

#### 1. Base Configuration (`base/kustomization.yaml`)

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - deployment.yaml
  - service.yaml
commonLabels:
  app: myapp
```

**deployment.yaml** (in `base/`):

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 2
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
      - name: myapp
        image: nginx:1.20
```

**service.yaml**:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: myapp-service
spec:
  type: ClusterIP
  selector:
    app: myapp
  ports:
  - port: 80
    targetPort: 80
```

***

#### 2. Dev Overlay (`overlays/dev/kustomization.yaml`)

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - ../../base
patchesStrategicMerge:
  - patch.yaml
```

**patch.yaml** (only changes dev-specific values):

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: myapp
        image: nginx:1.21
```

To apply the dev configuration to your cluster:

```bash
kubectl apply -k overlays/dev
```

The `-k` flag tells `kubectl` to use Kustomize to build and apply manifests.[1][5][6]

***

### Example: Production Overlay

**overlays/prod/patch.yaml**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 4
  template:
    spec:
      containers:
      - name: myapp
        image: nginx:1.23
```

Apply with:

```bash
kubectl apply -k overlays/prod
```

This deploys the same base configuration but customizes replicas and image version for production.[6][4]

***

### Real‑World Use Cases

1. **Multiple Environments Management**  
   Maintain a single base configuration for all environments (dev/staging/prod) and overlay environment‑specific changes such as image tags, resources, or Ingress settings.

2. **Team Collaboration**  
   Developers can safely adjust configuration previews (like replicas or environment variables) without touching production YAML files.

3. **GitOps Pipelines**  
   Kustomize integrates smoothly with CI/CD pipelines and GitOps tools (Flux, ArgoCD) for environment‑specific deployments.[5][3]

4. **Combining with Helm**  
   Many teams use Helm for packaging and use Kustomize to patch Helm‑generated files for organization‑specific tweaks without forking the chart.[4]

***

### Summary

| Feature | Description | Command |
|----------|--------------|---------|
| Tool Type | Native YAML overlay configuration manager | `kubectl apply -k` |
| Works On | Kubernetes manifests (Deployments, Services, ConfigMaps, etc.) | |
| Strengths | Layered configuration, no templates, built into kubectl | |
| Example Command | `kustomize build overlays/dev` or `kubectl apply -k overlays/dev` | |

Kustomize helps you **manage different Kubernetes environments cleanly**, promoting reusability and safer configuration updates without complex templating.[2][5][6][4]

https://kubernetes.io/docs/tasks/manage-kubernetes-objects/kustomization/
