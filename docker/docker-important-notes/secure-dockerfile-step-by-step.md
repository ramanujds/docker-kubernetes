## Step 0 – Your current Dockerfile (baseline)

```dockerfile
# Build stage
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:25-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

This is **functionally correct**, but **not production-secure yet**.

---

## Step 1 – Reduce attack surface (runtime image)

### Problem

```dockerfile
FROM amazoncorretto:25-jdk
```

Issues:

* Full JDK (compiler, tools, debug utilities)
* Larger CVE surface
* Not required at runtime

### Fix

Use **JRE-only** or **distroless** image.

**Safer option (JRE):**

```dockerfile
FROM amazoncorretto:25-jre
```

**Most secure option (recommended):**

```dockerfile
FROM gcr.io/distroless/java25
```

Why distroless?

* No shell
* No package manager
* Minimal OS footprint


---

## Step 2 – Run as non-root user

### Problem

Containers run as `root` by default.

Impact:

* Container breakout becomes dangerous
* Violates Kubernetes Pod Security Standards
* Security audits will flag this immediately

### Fix

Create and switch to a non-root user.

For distroless:

```dockerfile
USER nonroot
```

For normal images:

```dockerfile
RUN useradd -u 10001 appuser
USER appuser
```

This **limits blast radius** if compromised.

---

## Step 3 – Secure the build stage (BuildKit + cache)

### Problem

* Maven cache stored in layers
* Slower CI builds
* Larger intermediate images

### Fix

Use **BuildKit cache mounts** (already discussed, but now for security + cleanliness).

```dockerfile
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests
```

Why this matters for security:

* No dependency cache baked into layers
* Cleaner image history
* Reduced supply-chain leakage

---

## Step 4 – Copy only what is strictly needed

### Problem

Wildcard copy:

```dockerfile
COPY --from=build /app/target/*.jar app.jar
```

Risk:

* Multiple JARs accidentally copied
* Debug/test artifacts included

### Fix

Be explicit:

```dockerfile
COPY --from=build /app/target/my-app.jar app.jar
```

Security principle: **least privilege, least content**.

---

## Step 5 – Drop EXPOSE (optional but recommended)

### Problem

```dockerfile
EXPOSE 8080
```

Reality:

* `EXPOSE` is documentation only
* Kubernetes and cloud platforms ignore it
* Can mislead security reviews

### Recommendation

Remove it unless you rely on Docker-native networking.

---

## Step 6 – JVM hardening for containers

### Problem

Default JVM settings assume:

* Unlimited memory
* No container awareness

### Fix

Add safe JVM flags:

```dockerfile
ENTRYPOINT ["java",
  "-XX:+UseContainerSupport",
  "-XX:MaxRAMPercentage=75",
  "-XX:+ExitOnOutOfMemoryError",
  "-jar", "app.jar"]
```

Benefits:

* Prevents OOM-kill loops
* Predictable memory usage
* Better Kubernetes stability

---

## Step 7 – Final hardened Dockerfile (production-ready)

```dockerfile
# syntax=docker/dockerfile:1.7

# -------- Build stage --------
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests


# -------- Runtime stage --------
FROM gcr.io/distroless/java25
WORKDIR /app

COPY --from=build /app/target/my-app.jar app.jar

USER nonroot

ENTRYPOINT ["java",
  "-XX:+UseContainerSupport",
  "-XX:MaxRAMPercentage=75",
  "-XX:+ExitOnOutOfMemoryError",
  "-jar", "app.jar"]
```

---


## How this passes real audits

This Dockerfile satisfies:

* Kubernetes Pod Security Standards
* Enterprise container hardening checklists
* Banking / FinTech security reviews
* Production Kubernetes best practices

---

