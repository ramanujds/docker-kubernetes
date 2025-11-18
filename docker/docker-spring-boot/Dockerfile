# Build stage
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM amazoncorretto:25-jdk
WORKDIR /
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080