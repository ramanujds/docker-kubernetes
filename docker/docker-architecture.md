# Docker Architecture

![Docker Architecture](https://community.aws/_next/image?url=https%3A%2F%2Fassets.community.aws%2Fa%2F2k3Ctyi9cR2JG3wkNwFLN5bTV5X%2Farchitecture-docker-jpg.webp%3FimgSize%3D1280x710&w=828&q=75)

# **Docker Architecture** 

Docker follows a **client-server architecture** that consists of different components working together to manage containers efficiently. Below is an in-depth breakdown of Docker's architecture.

---

## **Key Components of Docker Architecture**
Docker is built on three main components:

### **1. Docker Client**
- The command-line interface (CLI) or graphical interface that users interact with.
- Sends commands (`docker run`, `docker ps`) to the **Docker daemon**.
- Can communicate with a local or remote daemon.

### **2.Docker Daemon (dockerd)**
- The core component that runs in the background, managing **containers, images, networks, and volumes**.
- Listens for Docker API requests from the **Docker Client**.
- Interacts with the OS kernel to create and manage containers.

### **3. Docker Objects (Images, Containers, Networks, Volumes)**
Docker works with different objects:

| Docker Object | Description |
|--------------|-------------|
| **Images**  | Read-only templates that contain the application and dependencies (e.g., `nginx:latest`) |
| **Containers** | Running instances of images (lightweight, isolated environments) |
| **Networks** | Virtual networks that connect containers |
| **Volumes** | Persistent storage for containers |

---



## **Communication Flow**
1. The **Docker Client** sends a request (`docker run nginx`) to the **Docker Daemon**.
2. The **Docker Daemon** pulls the **image** from **Docker Hub** (if not already available).
3. The **Container Runtime** (containerd) starts the **container** using **Linux namespaces** and **cgroups**.
4. Docker assigns **networking** and **storage** to the container.
5. The container runs until it’s stopped or removed.

---

## **Key Subsystems of Docker**
Docker relies on several **Linux kernel features** for containerization:

| Feature | Description |
|---------|------------|
| **Namespaces** | Isolate processes, networks, filesystems for containers. |
| **Cgroups** | Control CPU, memory, and disk usage per container. |
| **UnionFS** | Layered file system (AUFS, OverlayFS, Btrfs) for efficient image storage. |
| **Container Runtime** | `containerd` and `runc` manage low-level container execution. |

---

## **Docker Execution Flow**
### **Step 1: Running a Container**
```bash
docker run -d -p 8080:80 nginx
```
### **Step 2: Execution Process**
1. **Client → Daemon Communication**
    - The CLI sends a request to `dockerd`.
2. **Image Management**
    - If `nginx` is not available, Docker pulls it from **Docker Hub**.
3. **Container Creation**
    - Docker daemon creates a new **container** with its own **namespaces**.
4. **Networking Setup**
    - The container is assigned an IP address (`docker network ls`).
5. **Execution**
    - The container runs in an isolated environment.
6. **Monitoring**
    - Use `docker ps` to check running containers.

---

## **Container Runtimes**
Docker supports different **container runtimes** to execute containers:

| Runtime | Description |
|---------|------------|
| **containerd** | Default runtime for Docker, manages lifecycle operations. |
| **runc** | Low-level OCI-compliant runtime for running containers. |
| **CRI-O** | Lightweight runtime optimized for Kubernetes. |

---

## **Storage in Docker**
Docker uses different storage drivers for **images and volumes**:

### **1 Image Storage (UnionFS)**
- Uses **layered filesystems** for efficient storage.
- **Common storage drivers:**
    - `overlay2` (default for Linux)
    - `aufs` (deprecated)
    - `btrfs`
    - `zfs`

### **2 Persistent Storage (Volumes & Bind Mounts)**
- **Volumes** (Managed by Docker, stored under `/var/lib/docker/volumes/`)
- **Bind Mounts** (Directly maps host directory to container)

---

## **Networking in Docker**
Docker provides multiple networking options:

| Network Type | Description |
|-------------|------------|
| **Bridge** | Default network, allows communication between containers. |
| **Host** | Shares the host’s networking stack. |
| **Overlay** | Used in Docker Swarm for multi-host networking. |
| **Macvlan** | Assigns a unique MAC address to the container. |

### **Example: Listing Networks**
```bash
docker network ls
```

---

## **Orchestration & Scaling**
Docker supports container orchestration for managing multiple containers:

| Tool | Description |
|------|------------|
| **Docker Swarm** | Built-in clustering and load balancing. |
| **Kubernetes** | Advanced container orchestration platform. |

### **Example: Running Docker Swarm**
```bash
docker swarm init
docker service create --name web -p 80:80 nginx
```

---

## **Docker vs Virtual Machines (VMs)**
| Feature | Docker Containers | Virtual Machines (VMs) |
|---------|------------------|------------------|
| **Speed** | Lightweight, starts in seconds | Heavy, takes minutes |
| **Isolation** | Shares OS kernel | Full OS per VM |
| **Resource Usage** | Low (uses host OS) | High (requires full OS per VM) |
| **Portability** | High (runs anywhere) | Limited to OS/hypervisor |

---


