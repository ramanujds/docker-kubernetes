
## Managing Kubernetes Secrets from GCP Secret Manager (Using GCP Console)

---

### Step 1: Create a Secret in GCP Secret Manager

1. Go to GCP Console → Secret Manager: [https://console.cloud.google.com/security/secret-manager](https://console.cloud.google.com/security/secret-manager)
2. Click "Create Secret"
3. Name it `db-password`
4. Enter the secret value (e.g., `mySecurePassword123`)
5. Click "Create"

---

### Step 2: Create a Service Account for Kubernetes to Access the Secret

1. Go to IAM & Admin → Service Accounts: [https://console.cloud.google.com/iam-admin/serviceaccounts](https://console.cloud.google.com/iam-admin/serviceaccounts)
2. Click "Create Service Account"

    * Name: `eso-syncer`
3. Skip adding roles during creation
4. Click "Done"

#### Grant Secret Access Role

1. Go to Secret Manager → Click on `db-password`
2. Go to the "Permissions" tab
3. Click "Grant Access"
4. Add the service account: `eso-syncer@<project-id>.iam.gserviceaccount.com`
5. Role: Secret Manager Secret Accessor
6. Click Save

#### Generate and Download a Key

1. Go to the service account you just created
2. Go to the "Keys" tab → Click "Add Key" → JSON
3. Save the `key.json` file locally

---

### Step 3: Create a Kubernetes Secret from the JSON Key

```bash
kubectl create secret generic gcp-credentials \
  --from-file=key.json=key.json
```

---

### Step 4: Install External Secrets Operator (ESO)

```bash
kubectl apply -f https://github.com/external-secrets/external-secrets/releases/download/v0.9.10/install-crds.yaml
kubectl apply -f https://github.com/external-secrets/external-secrets/releases/download/v0.9.10/external-secrets.yaml
```

---

### Step 5: Create a SecretStore

```yaml
# secret-store.yaml
apiVersion: external-secrets.io/v1beta1
kind: SecretStore
metadata:
  name: gcp-secret-store
spec:
  provider:
    gcpsm:
      projectID: <YOUR_PROJECT_ID>
      auth:
        secretRef:
          secretAccessKeySecretRef:
            name: gcp-credentials
            key: key.json
```

Apply it:

```bash
kubectl apply -f secret-store.yaml
```

---

### Step 6: Create an ExternalSecret Resource

```yaml
# external-secret.yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: my-db-secret
spec:
  refreshInterval: 1h
  secretStoreRef:
    name: gcp-secret-store
    kind: SecretStore
  target:
    name: db-k8s-secret
  data:
    - secretKey: password
      remoteRef:
        key: db-password
```

Apply it:

```bash
kubectl apply -f external-secret.yaml
```

---

### Step 7: Use the Synced Secret in a Pod

```yaml
# pod.yaml
apiVersion: v1
kind: Pod
metadata:
  name: test-secret
spec:
  containers:
    - name: app
      image: busybox
      command: ["sh", "-c", "echo $DB_PASSWORD && sleep 3600"]
      env:
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-k8s-secret
              key: password
```

Apply and verify:

```bash
kubectl apply -f pod.yaml
kubectl logs test-secret
```

Expected output:

```
mySecurePassword123
```

---
