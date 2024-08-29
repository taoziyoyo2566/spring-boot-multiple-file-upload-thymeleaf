# Use a base image with JDK 17 for the build stage
#FROM maven:3.8.6-openjdk-17 AS builder
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy your project files into the Docker image
COPY pom.xml .
COPY src ./src

# Build the application skipping tests to speed up the build
RUN mvn clean install -DskipTests

# Use an image with JRE 17 for the runtime stage
FROM amazoncorretto:17

WORKDIR /home

# Copy the built jar file from the builder stage
COPY --from=builder /app/target/spring-boot-multiple-file-upload-thymeleaf-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:InitialRAMPercentage=70.0", "-XX:MinRAMPercentage=80.0", "-XX:MaxRAMPercentage=90.0", "-jar", "spring-boot-multiple-file-upload-thymeleaf-0.0.1-SNAPSHOT.jar"]

#ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:InitialRAMPercentage=70.0", "-XX:MinRAMPercentage=80.0", "-XX:MaxRAMPercentage=90.0", "-XX:-UseAdaptiveSizePolicy", "-XshowSettings:vm -version", "-jar", "spring-boot-multiple-file-upload-thymeleaf-0.0.1-SNAPSHOT.jar",">>","/home/logs.log"]