# Software Development Simulation

[![Software-Development-Simulation-Main-Build](https://github.com/MarkoDojkic/Software-Development-Simulation/actions/workflows/build.yml/badge.svg)](https://github.com/MarkoDojkic/Software-Development-Simulation/actions/workflows/build.yml)
![SonarQube Security Rating](https://img.shields.io/badge/SonarQube%20Security%20Rating-A-brightgreen)
![SonarQube Quality Gate](https://img.shields.io/badge/SonarQube%20Quality%20Gate-Passed-brightgreen)
![SonarQube Duplicated Lines](https://img.shields.io/badge/SonarQube%20Duplicated%20Lines-0%25-brightgreen)
![SonarQube LOC](https://img.shields.io/badge/SonarQube%20LOC-2000-blue)
![JaCoCo Coverage](https://img.shields.io/badge/JaCoCo%20Coverage-93.5%25-brightgreen)

**Note:** SonarQube information is based on the last GitHub Action run and is generated locally. As such, there is no direct link available to the SonarQube dashboard.

## Overview

The Software Development Simulation project is a web-based application designed to simulate and manage software development tasks. It utilizes Spring Boot for backend services, Spring Integration for messaging, and integrates with Swagger for API documentation.

## Features

- **Spring Boot Application**: Built with Spring Boot 3.3.3.
- **Spring Integration**: Configured with multiple channels for task management.
- **Swagger Documentation**: Integrated for API documentation and testing.
- **SonarQube Integration**: Quality and security analysis with all issues resolved.
- **JaCoCo Test Coverage**: 100% class and method test coverage.

## Setup

1. **Clone the Repository**

   ```bash
   git clone https://github.com/MarkoDojkic/Software-Development-Simulation.git

2. **Build the Project**
   
   ```bash
   mvn clean install

3. **Run the Application**

   ```bash
   mvn spring-boot:run

The application will run on port `21682`. RabbitMQ is required with rabbitmq_mqtt and rabbitmq_web_mqtt plugins enabled and active on port `15675`.

## Docker Setup

To build and run the application using Docker, follow these steps:

### Dockerfile

The `Dockerfile` is set up to build and package the application into a Docker image. It uses Maven to build the JAR file and then packages it into a minimal Java runtime image.

### Build and Run the Docker Image

1. **Build the Docker image:**

    ```bash
    docker build -t software-development-simulation .
    ```

2. **Run the Docker container:**

    ```bash
    docker run -p 5672:5672 -p 15672:15672 -p 15675:15675 -p 21682:21682 software-development-simulation
    ```

This will build the Docker image and run the application.
NOTE: Before running docker container make sure that no instances of `RabbitMQ` are already active because it will interfere with `docker image RabbitMQ` and messages won`t get through.

**Access Swagger UI**

Navigate to `http://localhost:21682/swagger-ui.html` to access the Swagger UI.

## Spring Integration Channels

The application utilizes various Spring Integration channels for processing different types of messages. Below is a summary of the key channels and their purposes:

1. **Error Channels**

   - `errorChannel`: Handles general errors.
   - `errorChannel.mqtt.input`: Sends error messages to MQTT for frontend integration.
   - `errorChannel.logFile.input`: Saves error messages to log file for persistent storage.

2. **Information Channels**

   - `information.input`: Receives general information.
   - `information.mqtt.input`: Sends information to MQTT for frontend integration.
   - `information.logFile.input`: Saves information to log file for persistent storage.

3. **Jira Activity Stream Channels**

   - `jiraActivityStream.input`: Receives Jira activity stream messages.
   - `jiraActivityStream.mqtt.input`: Sends Jira activity stream messages to MQTT for frontend integration.
   - `jiraActivityStream.logFile.input`: Saves Jira activity stream messages to log file for persistent storage.

4. **Epic Channels**

   - `epicMessage.input`: Receives messages related to epics with priority handling.

5. **Control Bus Channel**

   - `controlBus.input`: Handles control bus messages for managing flows.

6. **Current Sprint Channels**

   - `currentSprintEpic.input`: Receives epics for the current sprint.
   - `currentSprintUserStories`: Receives user stories for the current sprint using splitter from `currentSprintEpic.input`.

7. **In Progress Channels**

   - `inProgressEpic`: Receives epics that are currently in progress.
   - `inProgressUserStory`: Receives user stories that are currently in progress.

8. **Done Channels**

   - `doneEpics.output`: Receives completed epics.
   - `doneSprintUserStories.output`: Receives completed user stories.
   - `doneTechnicalTasks`: Receives completed technical tasks.

9. **Technical Task Channels**

   - `toDoTechnicalTasks`: Receives technical tasks that are to be done.
   - `trivialTechnicalTaskQueue.input` A`normalTechnicalTaskQueue.input`, `minorTechnicalTaskQueue.input`, `majorTechnicalTaskQueue.input`, `criticalTechnicalTaskQueue.input`, `blockerTechnicalTaskQueue.input`: Receive technical tasks of various priorities.
   - `trivialTechnicalTask`, `normalTechnicalTask`, `minorTechnicalTask`, `majorTechnicalTask`, `criticalTechnicalTask`, `blockerTechnicalTask`: Handle technical tasks based on priority.

Each channel is configured to handle specific message types and priorities, ensuring efficient processing and management of tasks and messages within the application. Channels that send messages to MQTT or save to log files are specifically set up to facilitate frontend integration and persistent logging.
   
   
