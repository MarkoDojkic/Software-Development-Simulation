# Stage 1: Build the Spring Boot application
FROM amazoncorretto:22-alpine-jdk AS build

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
FROM rabbitmq:management-alpine AS final

# Install necessary runtime packages
RUN apk add --no-cache openjdk22-jdk supervisor \
    && rabbitmq-plugins enable --offline rabbitmq_mqtt \
    && rabbitmq-plugins enable --offline rabbitmq_web_mqtt \
    && addgroup -S runtimeUsers \
    && adduser -S runtimeUser -G runtimeUsers

# Copy the Spring Boot application from the build stage
COPY --from=build /app/target/softwaredevelopmentsimulation-1.4.0.jar /app/software-development-simulation.jar

#Use nonroot user to run application
USER runtimeUser

# Supervisor configuration
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Add a health check for RabbitMQ
HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD ["sh", "-c", "nc -z localhost 5672 || exit 1"]

# Expose ports for RabbitMQ and Spring Boot application
EXPOSE 5672 15672 15675 21682

# Use supervisord to start and manage RabbitMQ and the Java application
ENTRYPOINT ["supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]

LABEL maintainer="Marko Dojkic <marko.dojkic@gmail.com>" \
      version="1.4.0" \
      description="Docker image for the Software Development Simulation Spring Boot application" \
      org.opencontainers.image.source="https://github.com/MarkoDojkic/Software-Development-Simulation" \
      org.opencontainers.image.documentation="https://github.com/MarkoDojkic/Software-Development-Simulation#readme" \
      org.opencontainers.image.licenses="MIT"
