# Persistent Volume Types in Kubernetes

## 1. **hostPath**

* **Description**: Maps a file or directory on the **host node** filesystem into the Pod.
* **Use Case**: **Testing & development** with Minikube or single-node clusters.
* **Limitations**:

    * Not suitable for multi-node clusters.
    * Not portable between nodes.

```yaml
hostPath:
  path: /mnt/data
```

---

## 2. **nfs** (Network File System)

* **Description**: Mounts an **external NFS share** into the cluster.
* **Use Case**:

    * Shared storage across Pods and nodes.
    * Use cases where multiple Pods need to read/write the same files (e.g., media servers, file storage).
* **Pros**:

    * Mature, POSIX-compliant shared filesystem.
* **Cons**:

    * Requires an external NFS server setup.

```yaml
nfs:
  server: 10.10.10.10
  path: /data
```

---

## 3. **gcePersistentDisk** (GCP)

* **Description**: Mounts a **Google Cloud Persistent Disk** into a Pod.
* **Use Case**: Durable block storage in **GKE** clusters.
* **Supports**:

    * ReadWriteOnce, ReadOnlyMany

```yaml
gcePersistentDisk:
  pdName: my-disk
  fsType: ext4
```

---

## 4. **awsElasticBlockStore (EBS)**

* **Description**: Mounts an **EBS volume** in **AWS** to a Pod.
* **Use Case**: Stateful apps in **EKS**.
* **Note**: Volume must be in the same **availability zone** as the node.

```yaml
awsElasticBlockStore:
  volumeID: vol-0abcd1234
  fsType: ext4
```

---

## 5. **azureDisk / azureFile**

* **azureDisk**: Block storage (good for databases).
* **azureFile**: Shared file storage (good for logs, user uploads).
* **Use Case**: Use in **AKS clusters** based on your storage type needs.

```yaml
azureDisk:
  diskName: mydisk
  diskURI: https://...

azureFile:
  secretName: azure-secret
  shareName: myshare
```

---


## 6. **local**

* **Description**: Mounts a local disk attached to the node.
* **Use Case**: High-performance workloads like databases where **latency is critical**.
* **Limitation**: Pod must run on the same node.

```yaml
local:
  path: /mnt/disks/ssd1
```

---

## 8. **CSI (Container Storage Interface)** – Recommended

* **Description**: New standard that supports a **plug-and-play** model for external storage providers.
* **Use Case**: When you use **vendor-managed** storage like:

    * NetApp, Portworx, Longhorn, OpenEBS, etc.
* **Flexibility**: Replaces in-tree plugins for better modularity.

