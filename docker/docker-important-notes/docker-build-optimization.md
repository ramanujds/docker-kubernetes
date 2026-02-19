## BuildKit-Optimized Dockerfile (with Maven cache)

```dockerfile
# syntax=docker/dockerfile:1.7

# ---------- Build stage ----------
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /app

# Copy only pom.xml first (better layer caching)
COPY pom.xml .

# Cache Maven dependencies using BuildKit
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (reuse Maven cache)
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests


# ---------- Runtime stage ----------
FROM amazoncorretto:25-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## What changed (and why it matters)

### 1. BuildKit syntax enabled

```dockerfile
# syntax=docker/dockerfile:1.7
```

This tells Docker to use **BuildKit features**, especially cache mounts.

---

### 2. Maven cache mounted instead of baked into layers

```dockerfile
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B
```

and

```dockerfile
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests
```

**What this does**

* `/root/.m2` is stored in a **persistent build cache**
* Dependencies are **NOT stored in the image layer**
* Cache survives across builds, branches, and CI runs

**Result**

* First build: normal speed
* Subsequent builds: **dramatically faster**
* Image stays clean and small

---

### 3. Layering strategy still intact

```dockerfile
COPY pom.xml .
```

comes before

```dockerfile
COPY src ./src
```

So:

* Dependency download runs **only if pom.xml changes**
* Code changes don’t trigger dependency downloads

BuildKit + layering = maximum efficiency.

---


## Real-world impact (Java + CI/CD)

### Local development

* Change one Java file → build completes in seconds
* No repeated dependency downloads

### CI pipelines (Jenkins / GitHub Actions)

* Maven cache reused across pipeline runs
* 50–80% build time reduction is common

### Kubernetes deployments

* Faster image builds
* Faster rollouts
* Smaller attack surface

---

## How to enable BuildKit (important)

### Local

```bash
export DOCKER_BUILDKIT=1
docker build -t my-app .
```

### CI (recommended)

Set environment variable:

```bash
DOCKER_BUILDKIT=1
```

Most modern Docker versions already enable it by default.

---

