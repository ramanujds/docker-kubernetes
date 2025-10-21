# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:21-jdk-slim
WORKDIR /
COPY --from=build /app/target/*.jar app.jar
RUN useradd -u 1000 appuser
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080