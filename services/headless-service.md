
## What is a Headless Service?

Normally, a Kubernetes Service provides a **single virtual IP (ClusterIP)** and load balances traffic across pods.

But for a **StatefulSet**, we *don’t* want load balancing —
we need each pod to have its own **stable DNS identity** (e.g., `mysql-0`, `mysql-1`, etc.).

So we create a **headless service**, by setting:

```yaml
clusterIP: None
```

This tells Kubernetes:

> Don’t give me a virtual IP. Instead, create **individual DNS records** for each pod.

---

## Example: MySQL StatefulSet with Headless Service

### Headless Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mysql
spec:
  clusterIP: None   # Headless service
  selector:
    app: mysql
  ports:
    - port: 3306
```

### StatefulSet

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql
spec:
  serviceName: mysql   # Important: Must match the service name above
  replicas: 3
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
        - name: mysql
          image: mysql:8
          ports:
            - containerPort: 3306
          env:
            - name: MYSQL_ROOT_PASSWORD
              value: rootpass
```

---

## How Kubernetes Resolves DNS for Headless Service

When the above StatefulSet and Service are deployed, Kubernetes automatically assigns **stable DNS hostnames** for each pod.

For example (assuming namespace = `default`):

| Pod Name  | DNS Hostname                              | Resolves To       |
| --------- | ----------------------------------------- | ----------------- |
| `mysql-0` | `mysql-0.mysql.default.svc.cluster.local` | Pod IP of mysql-0 |
| `mysql-1` | `mysql-1.mysql.default.svc.cluster.local` | Pod IP of mysql-1 |
| `mysql-2` | `mysql-2.mysql.default.svc.cluster.local` | Pod IP of mysql-2 |

This means:

* Each pod gets its **own unique DNS name**.
* The service name (`mysql`) is the **subdomain**.
* The pattern is:

  ```
  <pod-name>.<service-name>.<namespace>.svc.cluster.local
  ```

---

## How to Use It (Inside and Outside Cluster)

### Inside the same Kubernetes namespace

If another pod (e.g., an app) wants to connect to MySQL:

* To connect to the **primary instance** (e.g., mysql-0):

  ```
  mysql -h mysql-0.mysql -u root -p
  ```

* Or specify a JDBC URL (for Java apps):

  ```properties
  spring.datasource.url=jdbc:mysql://mysql-0.mysql:3306/mydb
  spring.datasource.username=root
  spring.datasource.password=rootpass
  ```

Kubernetes automatically resolves `mysql-0.mysql` → Pod IP of `mysql-0`.

---

### Inside a **different namespace**

If your app runs in another namespace (say `backend`):

Then the **fully qualified domain name (FQDN)** must include the namespace:

```bash
mysql -h mysql-0.mysql.default.svc.cluster.local -u root -p
```

or in your connection string:

```properties
spring.datasource.url=jdbc:mysql://mysql-0.mysql.default.svc.cluster.local:3306/mydb
```

---

### Connecting to any pod (for load balancing or replica reads)

You can use the **Service name directly** (`mysql`) to get a list of pod IPs.

For example:

```bash
mysql -h mysql -u root -p
```

Since it’s a **headless service**, this will resolve to multiple A records:

```
mysql.default.svc.cluster.local. -> [10.244.1.12, 10.244.2.9, 10.244.3.4]
```

Your client (e.g., MySQL driver) may pick one randomly or use them all (depending on client logic).

---

## Common Use Cases

| Use Case                                  | DNS to Use                                    | Description                          |
| ----------------------------------------- | --------------------------------------------- | ------------------------------------ |
| **Single-master DB** (like MySQL primary) | `mysql-0.mysql`                               | Connects to master pod               |
| **Replica reads**                         | `mysql-1.mysql`, `mysql-2.mysql`              | Connect to replica pods individually |
| **Client-side load balancing**            | `mysql` (headless service name)               | Resolves to all pods; client decides |
| **Cross-namespace access**                | `mysql-0.mysql.<namespace>.svc.cluster.local` | Use full FQDN                        |

---

## Example with JDBC Connection Strings

| Purpose                                                               | Example JDBC URL                                                               |
| --------------------------------------------------------------------- | ------------------------------------------------------------------------------ |
| Connect to a single MySQL pod                                         | `jdbc:mysql://mysql-0.mysql:3306/mydb`                                         |
| Connect to replicas manually                                          | `jdbc:mysql://mysql-1.mysql:3306,mydb`                                         |
| Connect to cluster via client-side load balancing (supported drivers) | `jdbc:mysql:loadbalance://mysql-0.mysql,mysql-1.mysql,mysql-2.mysql:3306/mydb` |
| Full qualified form (cross-namespace)                                 | `jdbc:mysql://mysql-0.mysql.default.svc.cluster.local:3306/mydb`               |

---

## Why the Headless Service Matters

Without `clusterIP: None`, all pods behind the Service share **one IP** — Kubernetes load balances requests randomly.
That’s fine for stateless apps, but **disastrous for databases** that need predictable pod addressing (e.g., master vs replica).

Headless services let StatefulSets expose **individual pod endpoints** to the rest of the cluster.

---

## Quick DNS Demo 

Run inside any pod:

```bash
kubectl exec -it some-pod -- nslookup mysql.default.svc.cluster.local
```

You’ll see multiple IPs (one per StatefulSet pod).
And:

```bash
kubectl exec -it some-pod -- nslookup mysql-0.mysql.default.svc.cluster.local
```

You’ll get exactly one IP (that of `mysql-0`).

