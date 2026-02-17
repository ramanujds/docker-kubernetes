## Why Kubernetes Probes Matter?

Kubernetes itself **does not know your application logic**.
It only knows:

* Is the container running?
* Is the app ready to receive traffic?
* Has the app fully started?

**Probes answer these questions**, so Kubernetes can:

* Restart broken pods
* Stop sending traffic to unhealthy pods
* Handle slow startups gracefully

---

## The 3 Types of Probes

### 1. Liveness Probe – “Is my app alive?”

**Purpose**

* Detects *deadlocked or hung applications*
* If this probe fails → **pod is restarted**

**When to use**

* App is running but not responding
* Memory leak, thread deadlock, infinite loop, etc.

**Spring Boot example**

* `/actuator/health/liveness`

---

### 2. Readiness Probe – “Can I send traffic to this pod?”

**Purpose**

* Controls traffic routing
* If this probe fails → **pod is removed from Service endpoints**
* Pod is NOT restarted

**When to use**

* App is up but:

    * DB is down
    * Kafka not reachable
    * Cache not warmed up

**Spring Boot example**

* `/actuator/health/readiness`

---

### 3. Startup Probe – “Has the app finished starting?”

**Purpose**

* Protects slow-starting apps
* Disables liveness & readiness checks until startup completes

**When to use**

* Spring Boot apps with:

    * DB migrations
    * Large context initialization
    * Cold JVM startup

**Spring Boot example**

* Same endpoint as liveness, but with relaxed timing

---

## How Spring Boot Actuator Helps

Spring Boot already gives you **production-ready health endpoints**.

### Enable probe-aware health groups

In `application.yml`:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
  endpoints:
    web:
      exposure:
        include: health
```

This enables:

* `/actuator/health/liveness`
* `/actuator/health/readiness`

---

## Typical Health Behavior

| Endpoint            | Used By             | Meaning                |
|---------------------|---------------------|------------------------|
| `/health/liveness`  | Liveness Probe      | App is not dead        |
| `/health/readiness` | Readiness Probe     | App can handle traffic |
| `/health`           | Humans / Monitoring | Overall app health     |

---

## Kubernetes Pod Configuration (Recommended)

### Deployment YAML (Spring Boot)

```yaml
containers:
  - name: springboot-app
    image: my-spring-app:1.0
    ports:
      - containerPort: 8080

    startupProbe:
      httpGet:
        path: /actuator/health/liveness
        port: 8080
      failureThreshold: 30
      periodSeconds: 10

    livenessProbe:
      httpGet:
        path: /actuator/health/liveness
        port: 8080
      initialDelaySeconds: 0
      periodSeconds: 10
      failureThreshold: 3

    readinessProbe:
      httpGet:
        path: /actuator/health/readiness
        port: 8080
      periodSeconds: 5
      failureThreshold: 3
```

---

## How This Works Together

1. **Pod starts**

    * Only `startupProbe` is active
2. **Startup completes**

    * Startup probe succeeds
3. **Readiness probe**

    * Traffic starts flowing
4. **Liveness probe**

    * Continuous health monitoring
5. **If readiness fails**

    * Traffic stops
6. **If liveness fails**

    * Pod restarts

---

## Spring Boot Example

### Readiness depends on DB

If DB is down:

* `/readiness` → `DOWN`
* `/liveness` → still `UP`
* Pod stays alive but **no traffic is sent**

This is exactly what you want in production.

---

## Common Mistakes

1. **Using the same endpoint for all probes**

    * Causes unnecessary restarts
2. **No startup probe**

    * Spring Boot gets killed during startup
3. **Aggressive liveness timings**

    * JVM GC pauses trigger restarts
4. **Exposing all actuator endpoints publicly**

    * Security risk

---

## Quick Rule of Thumb

* **Startup Probe** → “Give my app time”
* **Readiness Probe** → “Send traffic only when ready”
* **Liveness Probe** → “Restart me if I’m stuck”


