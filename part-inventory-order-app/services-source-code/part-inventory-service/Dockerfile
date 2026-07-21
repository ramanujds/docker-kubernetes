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
FROM amazoncorretto:25-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

#USER nonroot

ENTRYPOINT ["java","-jar", "app.jar"]