### **Docker Containers and Namespaces**

Docker uses **Linux namespaces** to create isolated environments for containers, ensuring that each container runs independently without interfering with others. Namespaces are a core part of Docker's containerization, providing process and resource isolation.

---

## **What is a Namespace?**
A namespace is a feature in the Linux kernel that isolates system resources between different processes. Each container in Docker gets its own set of namespaces, making it appear as if it has its own independent environment.

---

## **Types of Namespaces Used in Docker**
Docker uses multiple namespaces to isolate different system aspects:

| Namespace  | Description | Command to View |
|------------|------------|----------------|
| **PID Namespace** | Isolates process IDs so that processes inside a container cannot see processes outside it. | `lsns -t pid` |
| **NET Namespace** | Provides each container with its own network stack (IP address, routes, ports, etc.). | `lsns -t net` |
| **MNT Namespace** | Isolates filesystem mounts so that each container has its own root filesystem. | `lsns -t mnt` |
| **UTS Namespace** | Allows containers to have their own hostname and domain name. | `lsns -t uts` |
| **IPC Namespace** | Isolates inter-process communication (shared memory, semaphores, message queues). | `lsns -t ipc` |
| **User Namespace** | Enables user ID mapping for better security (UID/GID isolation). | `lsns -t user` |

---

## **How Namespaces Work in Docker?**
When you create a Docker container, it automatically assigns a new set of namespaces for **processes, networking, and filesystems**. Let's look at an example:

### **Example: Viewing Namespace Isolation**
#### **Step 1: Run an Nginx container**
```bash
docker run -d --name mynginx nginx
```
#### **Step 2: Find the container’s process ID (PID) on the host**
```bash
docker inspect --format '{{.State.Pid}}' mynginx
```
#### **Step 3: Check the namespaces of the container process**
```bash
lsns | grep <PID>
```
Each entry corresponds to a different namespace that the container is using.

---

## **Understanding Each Namespace with Use Cases**
### **1. PID Namespace (Process Isolation)**
- Ensures that processes inside a container **cannot see or kill** processes outside the container.
- **Example**: Running `ps aux` inside a container only shows the processes running within that container.

### **2. Network Namespace (Network Isolation)**
- Provides **separate networking** for each container.
- **Example**: Containers can have their own private IPs and communicate via virtual bridges like `docker0`.

### **3. Mount Namespace (Filesystem Isolation)**
- Ensures each container has its own **root filesystem**.
- **Example**: A container cannot access the host’s file system unless explicitly mounted.

### **4. UTS Namespace (Hostname Isolation)**
- Allows containers to have **different hostnames** than the host.
- **Example**: Running `hostname` inside a container shows a different name than the host.

### **5. IPC Namespace (Shared Memory Isolation)**
- Isolates inter-process communication (IPC).
- **Example**: Shared memory segments (`shm`) are **separate for each container**, preventing one container from reading another's IPC.

### **6. User Namespace (User ID Isolation)**
- Maps a container’s root user to a non-root user on the host for **security**.
- **Example**: Root inside a container can map to UID `1000` on the host, preventing host system modifications.

---

## **Real-World Use Case: Network Namespace Example**
Let's say you have two containers (`web1` and `web2`) and you want them to communicate **only within their own network**.

### **Step 1: Create a custom Docker network**
```bash
docker network create mynetwork
```
### **Step 2: Run two containers in this network**
```bash
docker run -d --name web1 --network mynetwork nginx
docker run -d --name web2 --network mynetwork nginx
```
### **Step 3: Test connectivity inside a container**
```bash
docker exec -it web1 ping web2
```
**Result:** The `web1` container can communicate with `web2`, but not with other containers outside this network.

---
