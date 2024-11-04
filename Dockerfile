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

# Download OpenJDK 22 packages from Alpine Edge (since it isn`t available in latest stable alpine 3.20)
# Check if openjdk22 is already installed, and install if not
RUN if ! java --version 2>/dev/null; then \
        echo "Installing OpenJDK 22..."; \
        apk add --no-cache curl; \
            curl -O https://dl-cdn.alpinelinux.org/alpine/edge/testing/x86_64/openjdk22-22.0.2_p9-r2.apk && \
            curl -O https://dl-cdn.alpinelinux.org/alpine/edge/testing/x86_64/openjdk22-jre-22.0.2_p9-r2.apk && \
            curl -O https://dl-cdn.alpinelinux.org/alpine/edge/testing/x86_64/openjdk22-jmods-22.0.2_p9-r2.apk && \
            curl -O https://dl-cdn.alpinelinux.org/alpine/edge/testing/x86_64/openjdk22-jdk-22.0.2_p9-r2.apk && \
            curl -O https://dl-cdn.alpinelinux.org/alpine/edge/testing/x86_64/openjdk22-demos-22.0.2_p9-r2.apk && \
            curl -O https://dl-cdn.alpinelinux.org/alpine/edge/testing/x86_64/openjdk22-doc-22.0.2_p9-r2.apk && \
            curl -O https://dl-cdn.alpinelinux.org/alpine/edge/testing/x86_64/openjdk22-jre-headless-22.0.2_p9-r2.apk && \
            curl -O https://dl-cdn.alpinelinux.org/alpine/edge/testing/x86_64/openjdk22-src-22.0.2_p9-r2.apk && \
            curl -O https://dl-cdn.alpinelinux.org/alpine/edge/testing/x86_64/openjdk22-static-libs-22.0.2_p9-r2.apk; \
        apk add --allow-untrusted ./*.apk && rm ./*.apk; \
    fi; \
    apk add --no-cache supervisor \
      && rabbitmq-plugins enable --offline rabbitmq_mqtt \
      && rabbitmq-plugins enable --offline rabbitmq_web_mqtt \
      && addgroup -S runtimeUsers && adduser -S runtimeUser -G runtimeUsers

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
