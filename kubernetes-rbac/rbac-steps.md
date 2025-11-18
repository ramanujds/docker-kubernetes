# Steps to set up RBAC for a user in the `dev` namespace

# 1) Apply manifests from the `kubernetes-rbac` directory
```bash
kubectl apply -f kubernetes-rbac/
```

## Test the setup

```bash

kubectl auth can-i get pods --as=system:serviceaccount:dev:dev-user --namespace=dev

```

```bash

kubectl auth can-i get deployment --as=system:serviceaccount:dev:dev-user --namespace=dev

```

# 2) Create token for the service account (note -n dev)
```bash
kubectl create token dev-user -n dev
```
# 3) Set credentials using that token
```bash
kubectl config set-credentials dev-user --token=$(kubectl create token dev-user -n dev)
```
# 4) Create a context that uses the correct cluster, user, and namespace
```bash
kubectl config set-context dev-user-context \
  --cluster=$(kubectl config view --minify -o jsonpath='{.clusters[0].name}') \
  --user=dev-user --namespace=dev
```

# 5) Use the context
```bash
kubectl config use-context dev-user-context
```
# 6) Verify access

```bash

kubectl auth can-i get pods
kubectl auth can-i get deployments
kubectl get pods
kubectl get deployments

```
