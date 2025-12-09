## How to build a docker image for a Java application using Amazon Corretto 25 JDK

1. **Create a Dockerfile**: This file contains instructions on how to build the Docker image. Here is a simple example:

```Dockerfile
FROM amazoncorretto:25-jdk
WORKDIR /app
COPY target/docker-java-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080
```


2. **Build the Docker Image**: Use the `docker build` command to create the image. Run this command in the directory where your Dockerfile is located:

```bash
docker build -t your-image-name .
```

3. **Run the Docker Container**: Once the image is built, you can run it using the `docker run` command:

```bash
docker run -p 8080:8080 your-image-name
```

