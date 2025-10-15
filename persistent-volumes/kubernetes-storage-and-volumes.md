# Kubernetes Storage and Volumes

## Why Volumes Are Needed

* Containers inside a Pod **lose all data** if the container crashes or the Pod is rescheduled.
* Volumes ensure **data persistence**, **sharing between containers**, or **integration with external storage**.

---

## How Kubernetes Manages Volumes

### 1. **Volume Types**

Kubernetes supports many types of volumes. The most common ones:

| Volume Type                   | Use Case                                 | Where Data Is Stored                             |
| ----------------------------- | ---------------------------------------- | ------------------------------------------------ |
| `emptyDir`                    | Temporary storage for a Pod              | On the node's disk (removed when Pod is deleted) |
| `hostPath`                    | Access to host machine files             | Directly on the node’s file system               |
| `persistentVolumeClaim (PVC)` | Persistent storage                       | External: NFS, GCE PD, EBS, Azure Disk, etc.     |
| `configMap` / `secret`        | Inject configuration or secrets as files | In-memory or mounted at runtime                  |
| `ephemeral`                   | Kubernetes 1.26+, short-lived PVC        | Node’s disk, destroyed after Pod lifecycle       |

---

### 2. **Where Is Data Stored?**

It depends on the volume type:

#### `emptyDir`:

* Data is stored in a temporary directory on the **node's local disk**.
* Removed once the Pod is deleted or rescheduled.

```yaml
volumes:
  - name: cache-volume
    emptyDir: {}
```

#### `hostPath`:

* Mounts a **specific path from the node’s host filesystem** into the container.

```yaml
volumes:
  - name: logs
    hostPath:
      path: /var/log/myapp
      type: Directory
```

> Warning: Not portable. Tightly coupled to node internals.

#### `persistentVolume` (PV) + `persistentVolumeClaim` (PVC):

* This is the **Kubernetes-native way** to request and manage external storage.
* Supports: NFS, AWS EBS, Azure Disk, GCE PD, CephFS, iSCSI, CSI drivers, etc.

Example flow:

* Admin provisions a **PersistentVolume**
* Developer requests it using a **PersistentVolumeClaim**
* K8s binds the PVC to a matching PV

```yaml
# PVC
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mypvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

```yaml
# Pod
volumes:
  - name: data
    persistentVolumeClaim:
      claimName: mypvc
```

Where is the data stored?

* Depends on the underlying **storage class**.

    * For AWS: on an **EBS volume**
    * For GCP: on a **Persistent Disk**
    * For Minikube/Docker Desktop: a **local path on the host** (e.g., `/tmp` or a Docker volume mount)

---

### 3. **StorageClass: Dynamic Provisioning**

Instead of pre-creating PVs, you can dynamically create them using `StorageClass`.

```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: standard
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp2
```

* The PVC will automatically trigger dynamic provisioning via the `StorageClass`.

---

### 4. **Node Storage Location (Local Clusters)**

If you're using:

* **Minikube/Docker Desktop**: data is stored in a path inside the VM or Docker mount
* **GKE/EKS/AKS**: data is stored on **cloud block storage**
* **Bare metal**: depends on configured volume plugins like NFS, iSCSI, etc.

You can inspect mounts with:

```bash
kubectl describe pod <pod-name>
kubectl get pvc
kubectl get pv
```

Or on the node:

```bash
# Check where the volume is mounted
mount | grep kubelet
```

---

## Volume Lifecycle Summary

| Lifecycle Scope                     | Volume Type                                      |
| ----------------------------------- | ------------------------------------------------ |
| Pod lifetime only                   | `emptyDir`, `ephemeral`, `configMap`, `secret`   |
| Node-level                          | `hostPath`, `local` volumes                      |
| Persistent across pod/node restarts | `persistentVolumeClaim` (backed by real storage) |

---

## Best Practices

* Use **PVC + StorageClass** for cloud-native, portable storage.
* Avoid `hostPath` unless absolutely needed (e.g., log scraping, machine-specific data).
* Use `ReadWriteOnce`, `ReadOnlyMany`, or `ReadWriteMany` based on access needs.
* Mount secrets/configMaps as files instead of environment variables if they’re large or need rotation.

