# Use Quarkus native base image for building
FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:23.1-java21 AS builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml and resolve dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Use a lightweight JDK image for the final stage
FROM registry.access.redhat.com/ubi8/openjdk-21-runtime:1.18

# Set the working directory
WORKDIR /app

# Copy the built jar
COPY --from=builder /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder /app/target/quarkus-app/*.jar /deployments/
COPY --from=builder /app/target/quarkus-app/app/ /deployments/app/
COPY --from=builder /app/target/quarkus-app/quarkus/ /deployments/quarkus/

# Set environment variables
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

# Set the entry point
ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]