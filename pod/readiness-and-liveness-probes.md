Kubernetes provides three types of **probes** to monitor and manage the health and readiness of containers in a pod:

### 1. Liveness Probe
- **Purpose:** Checks if a container is alive (running properly).
- **What happens on failure:** If this probe fails, Kubernetes restarts the container, helping recover from deadlocks or crashes.
- **Use case:** Useful for applications that might get stuck or hang.
- **How to use:** Define a check endpoint, command, or TCP socket in the pod spec under `livenessProbe`. For example, an HTTP GET to `/health` endpoint or a command that returns a success status.[1][2][4]

### 2. Readiness Probe
- **Purpose:** Checks if a container is ready to receive traffic.
- **What happens on failure:** If it fails, the pod is temporarily removed from service endpoints, so no traffic is sent to it until it’s ready again.
- **Use case:** Ideal for applications that need startup time, load external resources, or recover from overload.
- **How to use:** Configured similarly to Liveness probes using HTTP, TCP, or command checks, but under `readinessProbe` in pod spec.[2][3][6][1]

### 3. Startup Probe
- **Purpose:** Checks if the application inside the container has started successfully.
- **What happens on failure:** Until the startup probe succeeds, readiness and liveness probes are disabled to avoid premature restarts.
- **Use case:** Designed for slow-starting containers where startup time might exceed liveness/readiness probe timeouts.
- **How to use:** Defined under `startupProbe` in pod spec and run only during container startup.[5][8][1]

### Probe Types (Applicable to all probes)
Each probe can be configured using one of three methods:
- **HTTP GET:** Kubernetes sends an HTTP GET request to a specified path and port inside the container. Success means response codes 200–399.
- **TCP Socket:** Kubernetes attempts to open a TCP connection on a specified port. Success if connection established.
- **Exec Command:** Kubernetes runs a command inside the container. Success if command exits with 0 status.

### Example of Configuring a Liveness Probe (HTTP)

```yaml
livenessProbe:
  httpGet:
    path: /healthz
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
  timeoutSeconds: 3
  failureThreshold: 3
```

This will start checking the health of the container after 10 seconds delay, every 5 seconds, and consider it failed if no response in 3 seconds for 3 consecutive times.

### Summary Use Cases for Each Probe

| Probe Type     | Purpose                                   | When To Use                               |
|----------------|-------------------------------------------|------------------------------------------|
| Liveness Probe | Check if container needs restart          | To catch crashes or deadlocks            |
| Readiness Probe| Check if container is ready for traffic   | For startup delays or overloaded states  |
| Startup Probe  | Check if container has started             | For slow-starting applications            |

