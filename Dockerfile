# Stage 1: Build the Spring Boot application
FROM amazoncorretto:21-alpine AS build

# Install necessary build tools
RUN apk add --no-cache maven

# Set the working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml /app/
COPY src /app/src/

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create an image with RabbitMQ and your Spring Boot application
FROM rabbitmq:3.13.7-management-alpine AS final

# Install necessary runtime packages
RUN apk add --no-cache openjdk21-jdk \
    && rabbitmq-plugins enable --offline rabbitmq_mqtt \
    && rabbitmq-plugins enable --offline rabbitmq_web_mqtt

# Copy the Spring Boot application from the build stage
COPY --from=build /app/target/softwaredevelopmentsimulation-1.3.0.jar /app/software-development-simulation.jar

# Add a health check for RabbitMQ
HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD nc -z localhost 5672 || exit 1

# Expose ports for RabbitMQ and Spring Boot application
EXPOSE 5672 15672 15675 21682

# Start RabbitMQ and wait for it to be ready before starting the Spring Boot application
CMD sh -c "rabbitmq-server & \
           until nc -z localhost 5672; do \
             echo 'Waiting for RabbitMQ...'; \
             sleep 1; \
           done; \
           java -jar /app/software-development-simulation.jar"

LABEL maintainer="Marko Dojkic <marko.dojkic@gmail.com>" \
      version="1.3.0" \
      description="Docker image for the Software Development Simulation Spring Boot application" \
      org.opencontainers.image.source="https://github.com/MarkoDojkic/Software-Development-Simulation" \
      org.opencontainers.image.documentation="https://github.com/MarkoDojkic/Software-Development-Simulation#readme" \
      org.opencontainers.image.licenses="MIT"