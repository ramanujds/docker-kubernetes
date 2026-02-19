## How Does Replication Actually Get Set Up?

There are only **3 real ways** in practice.

---

# Way 1 — Operator (Best / Production Way)

![Image](https://docs.percona.com/percona-operator-for-mysql/pxc/assets/images/operator.svg)

This is how serious teams do it.

You install a **MySQL Operator**, for example:

* Oracle MySQL Operator
* Percona Operator
* Bitnami Operator

What operator does:

```
You: "I want 3 MySQL replicas"

Operator:
✔ Creates StatefulSet
✔ Configures my.cnf
✔ Creates users
✔ Runs replication SQL
✔ Sets primary
✔ Monitors health
✔ Handles failover
```

So:

Operator is the “DB Admin” inside Kubernetes.

You never run replication commands manually.

---

# Way 2 — Helm Chart

Many Helm charts (Bitnami, etc.) do basic replication.

You install like:

```bash
helm install mysql bitnami/mysql \
  --set architecture=replication \
  --set replicaCount=2
```

Behind the scenes, chart:

* Generates configs
* Runs init scripts
* Sets up master/slave

Still automated, but less smart than operator.

---

## Way 3- Manual

Complex and error-prone.

# What Operator Really Does Internally

When a new pod comes up:

1. Operator waits for mysql-1 to be Ready
2. Connects to mysql-0
3. Takes snapshot
4. Copies data
5. Runs CHANGE MASTER
6. Starts replication

All automatic.

You never see it.

## How to Run MySQL on K8s

If you’re serious about running DBs on K8s:

Learn **MySQL Operator** next.

It’s industry standard now.


