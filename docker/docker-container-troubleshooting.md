# **Troubleshooting Docker Containers**



## **1. Checking Container Logs** 

If a container is failing or behaving unexpectedly, check its logs.

### **Command:**
```bash
docker logs <container_id>
```
### **Example:**
```bash
docker logs myapp
```
### **Use Case:**
If a container crashes immediately, logs may show an error such as:
```
Error: Cannot connect to database at localhost:5432
```
This might indicate a networking issue or that the database container is not running.

#### **Live Streaming Logs**
To continuously watch logs:
```bash
docker logs -f myapp
```

---

## **2. Checking Container Status** 

View all running and stopped containers.

### **Command:**
```bash
docker ps -a
```
### **Example Output:**
```
CONTAINER ID  IMAGE      STATUS                      PORTS
123abc        nginx      Exited (1) 5 minutes ago    80->8080
```
If the container **exited**, it likely failed due to an error.

---

## **3. Restarting a Container** 

If a container is stuck or failing, restart it.

### **Command:**
```bash
docker restart <container_id>
```
### **Example:**
```bash
docker restart myapp
```

---

## **4. Inspecting Container Details** 

Retrieve detailed information about a container.

### **Command:**
```bash
docker inspect <container_id>
```
### **Example:**
```bash
docker inspect myapp | grep -i "ipaddress"
```
### **Use Case:**
- Find IP address, environment variables, mount points, etc.
- Check if container is using the expected network.

---

## **5. Entering a Running Container (Exec)** 

Sometimes, you need to access a container to debug issues.

### **Command:**
```bash
docker exec -it <container_id> bash
```
### **Example:**
```bash
docker exec -it myapp bash
```
or, for Alpine-based containers:
```bash
docker exec -it myapp sh
```
### **Use Case:**
- Check running processes: `ps aux`
- Inspect logs: `cat /var/log/app.log`
- Debug network connectivity: `curl http://backend-service`

---

## **6. Checking Resource Usage (Performance Issues)** 

If a container is using too much CPU or memory, check its stats.

### **Command:**
```bash
docker stats
```
### **Example Output:**
```
CONTAINER   CPU %   MEM USAGE / LIMIT
myapp       85.6%   1.2GB / 2GB
```
### **Use Case:**
- If memory usage is high, consider adding memory limits to the container:
  ```bash
  docker run -d --memory="1g" myapp
  ```

---

## **7. Troubleshooting Network Issues** 

### **Check all networks**
```bash
docker network ls
```
### **Inspect a network**
```bash
docker network inspect <network_name>
```
### **Test connectivity between containers**
```bash
docker exec -it <container_id> ping <other_container>
```
### **Example:**
```bash
docker exec -it web1 ping web2
```
### **Use Case:**
If two containers cannot communicate, check:
- They are on the same network.
- The exposed ports are correctly mapped (`docker ps`).
- The application inside is listening on the right interface.

---

## **8. Checking File and Volume Mounts** 📂

If a container cannot access a file or directory, check mounted volumes.

### **Command:**
```bash
docker inspect <container_id> | grep Mounts
```
### **Example:**
```bash
docker inspect myapp | grep Mounts
```
### **Use Case:**
- Ensure the volume is mounted correctly.
- If files are missing, check if they exist on the host.

---

## **9. Cleaning Up Docker Resources** 

If the system is running out of disk space, clean up unused containers and images.

### **Remove all stopped containers**
```bash
docker container prune
```
### **Remove all unused images**
```bash
docker image prune -a
```
### **Remove unused networks**
```bash
docker network prune
```
### **Remove everything (Be Careful!)**
```bash
docker system prune -a
```

---

## **10. Checking Docker Daemon and System Logs** 

If Docker itself is not running properly:

### **Check Docker daemon status**
```bash
systemctl status docker
```
### **Check Docker daemon logs**
```bash
journalctl -u docker --no-pager | tail -n 50
```
### **Restart Docker service**
```bash
systemctl restart docker
```
### **Use Case:**
If Docker fails to start, check logs for errors like:
```
Error: unable to connect to the Docker daemon
```
This may indicate a permissions issue (`sudo usermod -aG docker $USER`).
