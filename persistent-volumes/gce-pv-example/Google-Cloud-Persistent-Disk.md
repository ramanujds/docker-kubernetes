## Hands-on example of using a **Google Cloud Persistent Disk** (GCE PD) as a **Persistent Volume (PV)** in a GKE cluster.


## Prerequisites:

* A GKE cluster is running.
* You’ve created a Persistent Disk using this command:

```bash
gcloud compute disks create --size=10GB --zone=us-central1-a my-gce-disk
```

