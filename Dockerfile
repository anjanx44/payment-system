# Use a Maven image to build the application
FROM maven:3.8-amazoncorretto-21 AS builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml and other necessary files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -Dquarkus.package.type=uber-jar

# Use a smaller image for the final artifact
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*-runner.jar app.jar

# Set the entrypoint to run the Quarkus application
ENTRYPOINT ["java", "-jar", "app.jar"]