## 1. Debugging with Logs 

### Why logs matter most

In containers:

* Containers are **ephemeral**
* You usually **don’t SSH**
* Logs are often the **only evidence** after a crash

Golden rule:

> If it’s not in logs, it didn’t happen.


---

### How logging works in containers

Best practice:

* Write logs to **stdout / stderr**
* Let the platform collect them

Bad (legacy):

```text
/var/log/app.log
```

Good:

```text
System.out / logger → stdout
```

Docker example:

```bash
docker logs my-container
docker logs -f my-container
docker logs --since 10m my-container
```

Kubernetes example:

```bash
kubectl logs pod-name
kubectl logs pod-name -c container-name
kubectl logs --previous pod-name
```

---

### Common log-based debugging scenarios

| Symptom                       | What to check             |
| ----------------------------- | ------------------------- |
| Container crashes immediately | Startup logs              |
| OOMKilled                     | JVM memory logs           |
| App not reachable             | Port binding logs         |
| Config issues                 | Environment variable logs |

Tip:
Always log:

* App startup config
* Active profiles
* Memory limits
* External dependency status

---

## 2. Debugging with `exec` (when logs are not enough)

### What is `exec`?

`exec` lets you **enter a running container** to inspect its state.

Docker:

```bash
docker exec -it my-container sh
```

Kubernetes:

```bash
kubectl exec -it pod-name -- sh
```

Use cases:

* Inspect files
* Check environment variables
* Validate config mounts
* Test network connectivity

### Important security note

* Distroless images **do not have a shell**
* This is intentional for security

So how do you debug?

---

### Debugging distroless containers 

#### Option 1: Ephemeral debug container (best practice)

Attach a temporary debug container **to the same pod**.

Kubernetes:

```bash
kubectl debug pod-name -it --image=busybox
```

You can:

* Inspect volumes
* Test networking
* Curl internal services

#### Option 2: Debug build

Maintain a separate debug image:

```dockerfile
FROM amazoncorretto:25-jdk
```

Use only in non-prod.

---

## 3. Debugging with Port Forwarding

When the app is running but misbehaving.

Kubernetes:

```bash
kubectl port-forward pod-name 8080:8080
```

Use cases:

* Hit health endpoints
* Test APIs locally
* Attach debugger

---

## 4. Profiling Containers (advanced but critical)

Logs and exec tell **what happened**.
Profiling tells **why it’s happening**.

---

## JVM profiling inside containers

### Common problems

* High CPU
* Memory leaks
* Thread starvation
* Slow GC

---

### Tools you should know

#### 1. JDK tools (if available)

* `jcmd`
* `jstack`
* `jmap`
* `jfr`

Example:

```bash
jcmd <pid> GC.heap_info
jstack <pid>
```

Requires:

* JDK-based image
* Or debug sidecar

---

#### 2. Java Flight Recorder (JFR)

Low-overhead, production-safe.

```bash
jcmd <pid> JFR.start
jcmd <pid> JFR.stop filename=recording.jfr
```

Analyze offline.

---

#### 3. Async-profiler

Used by:

* Netflix
* Uber
* Large-scale JVM teams

Captures:

* CPU
* Memory
* Locks

Works well inside containers.


## 5. Resource-level debugging (Docker & Kubernetes)

### Memory issues

Docker:

```bash
docker stats
```

Kubernetes:

```bash
kubectl top pod
kubectl describe pod
```

Check:

* OOMKilled events
* CPU throttling
* Memory limits vs JVM config

---

### Network debugging

Inside container:

```bash
curl service-name
nslookup service-name
```

From node or debug pod:

* DNS resolution
* Service endpoints
* Network policies

---

## 6. Debugging crashes and restarts

### CrashLoopBackOff

Steps:

1. `kubectl logs --previous`
2. Check exit code
3. Describe pod:

```bash
kubectl describe pod pod-name
```

Common causes:

* Wrong JVM memory settings
* Missing config
* Permission issues
* Failing health checks

---

## 7. Production-safe debugging strategy

### What you should do in prod

* Logs → Metrics → Traces
* Read-only inspection
* Profiling with low overhead
* No shell access

### What you should NOT do

* SSH into nodes
* Modify running containers
* Install tools on the fly
* Restart blindly

---

## Debugging hierarchy (mental model)

1. Logs (always first)
2. Metrics (CPU, memory, GC)
3. Exec / debug container
4. Profiling tools
5. Code-level fixes

---

## Real-world use cases

### Microservices in Kubernetes

* Debug failing pod using logs + previous logs
* Attach ephemeral container
* Profile CPU spike

### CI/CD debugging

* Compare image behavior across environments
* Validate config at runtime
* Detect missing files or permissions

### Incident response

* Capture heap dump
* Export JFR
* Analyze offline
* Fix root cause, not symptoms

---

## Final advice 

> **Design for debuggability**, not just correctness.

That means:

* Structured logging
* Health endpoints
* Metrics exposed
* Debug strategy documented

