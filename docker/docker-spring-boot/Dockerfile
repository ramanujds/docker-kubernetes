# Run stage
FROM amazoncorretto:25-jdk
WORKDIR /app
COPY target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080