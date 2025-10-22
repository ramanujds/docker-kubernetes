# Persistent Volumes in Kubernetes

## 1. What is a Persistent Volume (PV)?

A **Persistent Volume** is a **pre-provisioned storage resource** in the cluster. It can come from a variety of backends:

* Local disk
* NFS (Network File System)
* GCP Persistent Disk
* AWS EBS
* Azure Disk, etc.

Think of it like a physical **hard drive** that Kubernetes can plug into Pods.

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: my-pv
spec:
  capacity:
    storage: 1Gi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: /mnt/data
```

---

## 2. What is a Persistent Volume Claim (PVC)?

A **Persistent Volume Claim** is a **request for storage** by a user (or Pod). The claim specifies:

* How much storage is needed
* Access mode (ReadWriteOnce, ReadOnlyMany, ReadWriteMany)

Kubernetes then **binds** the PVC to a suitable PV.

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: my-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 500Mi
```

---

## 3. How Pod Uses a PVC

A Pod doesn't mount the PV directly. Instead, it mounts the **PVC**, which abstracts the underlying storage.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-pod
spec:
  containers:
    - name: app
      image: busybox
      command: ["sh", "-c", "echo 'Hello' >> /data/out.txt && sleep 3600"]
      volumeMounts:
        - mountPath: /data
          name: storage
  volumes:
    - name: storage
      persistentVolumeClaim:
        claimName: my-pvc
```

---

## Access Modes

| Mode            | Description                            |
| --------------- | -------------------------------------- |
| `ReadWriteOnce` | Mounted as read-write by a single node |
| `ReadOnlyMany`  | Mounted as read-only by many nodes     |
| `ReadWriteMany` | Mounted as read-write by many nodes    |

---

## Use Case Examples

| Use Case     | Description                                          |
| ------------ | ---------------------------------------------------- |
| **Database** | Store DB files on persistent disk                    |
| **Logging**  | Store logs even if Pod restarts                      |
| **Uploads**  | Save user-uploaded files outside container lifecycle |

---

## Summary

* **PV**: The physical or cloud storage abstraction.
* **PVC**: A request for storage.
* **Pod uses PVC** to persist data.


