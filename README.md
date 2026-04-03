This repository is intended for learning, experimentation, and reference purposes. It is not designed as a production-grade system.

# Kafka Learning Lab

A structured Spring Boot reference project that shows how REST requests can publish Kafka events while keeping code testable and easy to reason about.

## Overview

This project exposes an HTTP API that accepts a message, publishes it to Kafka, stores a local mirror in H2, and returns read models through a retrieval endpoint.

This problem matters in real systems because many services need a clear boundary between synchronous API handling and asynchronous event processing. Understanding this boundary helps teams design resilient systems with lower coupling.

## Real-World Context

Common production usage patterns:

- Event-driven microservices publishing domain events such as order-created or payment-settled.
- Operational APIs emitting events for analytics and downstream automation.
- Internal platforms that standardize service-to-service communication through Kafka topics.

Example use cases:

- E-commerce checkout publishes an order event consumed by inventory, billing, and notification services.
- Fintech APIs emit transaction events for compliance and fraud pipelines.
- SaaS backends publish tenant activity events to near real-time reporting streams.

## What This Repo Demonstrates

- Layered design with clear responsibilities across controller, service, stream, repository, and model packages.
- Request validation at the API edge.
- Synchronous API workflow combined with asynchronous Kafka consumption.
- Typed application configuration using `@ConfigurationProperties`.
- Unit tests, web-layer tests, and integration tests with embedded Kafka.
- Teaching-oriented documentation comments that explain design trade-offs.

## Architecture / Component Flow

### Components and responsibilities

- MessageController: Receives HTTP requests and enforces payload validation.
- MessageService: Orchestrates publish and persist steps for one coherent use case.
- KafkaMessageProducer: Encapsulates KafkaTemplate publish logic.
- KafkaMessageConsumer: Demonstrates asynchronous consumption and Kafka metadata visibility.
- KafkaMessageRepository: Reads/writes local mirror records in deterministic order.
- KafkaLabProperties: Centralized, validated topic and consumer-group configuration.

### Step-by-step request lifecycle

1. Client sends `POST /api/messages` with JSON payload.
2. Controller validates the request and delegates to the service.
3. Service publishes the message to the configured Kafka topic.
4. Service persists a mirror record in H2 for observability.
5. Service returns an API response with topic, status, and timestamp.
6. Consumer receives the event asynchronously and logs partition/offset metadata.
7. Client can call `GET /api/messages` to inspect stored data.

### Component flow diagram

```text
Client
  |
  v
MessageController (HTTP + validation)
  |
  v
MessageService (orchestration)
  |\
  | \--> KafkaMessageProducer --> Kafka Topic (learning-lab.kafka.topic-name)
  |
  +----> KafkaMessageRepository --> H2 in-memory table
                                  |
                                  v
                             GET /api/messages

KafkaMessageConsumer <-- Kafka Topic (async listener, consumer group)
```

### Engineering trade-offs and scalability notes

- The sample uses publish-then-persist ordering to keep the learning flow simple and explicit.
- API handling is synchronous for easier debugging; event handling is asynchronous to show decoupling.
- H2 is chosen for fast local feedback, but it is ephemeral and not a durability strategy.
- The consumer is intentionally side-effect-light to avoid hiding core Kafka concepts behind business complexity.
- Real deployments would typically add retries, dead-letter topics, idempotency, monitoring, and schema evolution controls.

## Tech Stack

- Java 17
- Spring Boot 2.7.x
- Spring Web
- Spring Kafka
- Spring Data JPA
- H2 (in-memory)
- Gradle
- JUnit 5, Mockito, MockMvc, spring-kafka-test

## Project Structure

```text
.
|-- src
|   |-- main
|   |   |-- java/org/kafkalab
|   |   |   |-- SpringKafkaLearningLabApplication.java
|   |   |   |-- config
|   |   |   |-- controller
|   |   |   |-- data
|   |   |   |-- model
|   |   |   |-- service
|   |   |   `-- stream
|   |   `-- resources
|   |       `-- application.yml
|   `-- test
|       `-- java/org/kafkalab
|           |-- controller
|           |-- service
|           |-- stream
|           `-- MessageFlowIntegrationTest.java
|-- compose.yml
|-- build.gradle
|-- settings.gradle
|-- README.md
`-- LICENSE
```

## How to Run Locally

### Simplest working path

1. Start Kafka with Docker Compose:

```bash
docker compose up -d
```

2. Build the project:

```bash
./gradlew clean build
```

Windows PowerShell:

```powershell
.\gradlew.bat clean build
```

3. Run the application:

```bash
./gradlew bootRun
```

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

4. Stop local Kafka when finished:

```bash
docker compose down
```

### Optional advanced setup (native Kafka on Windows)

Use this only if Docker is not available.

```powershell
$root = "C:\kfk"
New-Item -ItemType Directory -Path $root -Force | Out-Null
curl.exe -k -L "https://archive.apache.org/dist/kafka/3.7.2/kafka_2.13-3.7.2.tgz" -o "$root\kafka_2.13-3.7.2.tgz"
tar -xzf "$root\kafka_2.13-3.7.2.tgz" -C $root
```

```powershell
$clusterId = & "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-storage.bat" random-uuid
& "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-storage.bat" format -t $clusterId.Trim() -c "C:\kfk\kafka_2.13-3.7.2\config\kraft\server.properties"
```

```powershell
$env:KAFKA_HEAP_OPTS = "-Xmx1G -Xms1G"
& "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-server-start.bat" "C:\kfk\kafka_2.13-3.7.2\config\kraft\server.properties"
```

## How to Run Tests

```bash
./gradlew clean test
```

Windows PowerShell:

```powershell
.\gradlew.bat clean test
```

Test coverage includes:

- Service-level unit tests.
- Producer unit tests.
- Controller web-layer tests using MockMvc.
- End-to-end integration test with embedded Kafka.

## Example Usage

Publish a message:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/messages" -ContentType "application/json" -Body '{"data":"hello kafka"}'
```

Read stored messages:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/messages"
```

Quick verification flow:

1. Start Kafka.
2. Start the application.
3. Publish a message.
4. Read messages and confirm payload is stored.

## Real Kafka Verification Runbook

This runbook verifies the application against a real Kafka broker (not embedded test Kafka).

### 1) Start Kafka

Preferred (Docker):

```bash
docker compose up -d
docker compose ps
```

If Docker is unavailable, start native Kafka on Windows:

```powershell
$env:KAFKA_HEAP_OPTS = "-Xmx1G -Xms1G"
& "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-server-start.bat" "C:\kfk\kafka_2.13-3.7.2\config\kraft\server.properties"
```

Optional broker check:

```powershell
& "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-topics.bat" --bootstrap-server localhost:9092 --list
```

### 2) Start the application

If 8080 is already occupied, run on 8081:

```powershell
.\gradlew.bat bootRun --args="--server.port=8081"
```

### 3) Publish and read using the API

```powershell
$post = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/messages" -ContentType "application/json" -Body '{"data":"real-kafka-verification"}'
$get = Invoke-RestMethod -Method Get -Uri "http://localhost:8081/api/messages"
$post | ConvertTo-Json -Depth 5
$get | ConvertTo-Json -Depth 5
```

### 4) Expected verification signals

- POST response includes `topicName: "companies"` and success status.
- GET response includes the newly stored payload.
- Application logs show producer and consumer events for the same message.

### 5) Stop local processes

Docker:

```bash
docker compose down
```

Native Kafka:

- Stop the Kafka terminal with `Ctrl+C`.
- Stop the Spring Boot terminal with `Ctrl+C`.

## Learning Outcomes

After working through this project, you should be able to:

- Explain data flow from HTTP request to Kafka event and local persistence.
- Describe component boundaries in a layered Spring Boot application.
- Compare synchronous request handling with asynchronous event consumption.
- Write focused tests across unit, web, and integration scopes.
- Discuss architectural trade-offs in a teaching-friendly event-driven design.

## Limitations

This project is intentionally simplified and not production-ready.

- No authentication or authorization.
- No schema registry or event versioning strategy.
- No retry policy, dead-letter topic, or idempotent processing workflow.
- No advanced observability stack (metrics/tracing dashboards).
- H2 is in-memory only and data is lost on restart.

## Future Improvements

- Add centralized exception handling and richer API error contracts.
- Add outbox-pattern variant to discuss consistency guarantees.
- Add idempotent-consumer and retry/DLQ learning examples.
- Add metrics and tracing demonstrations with Micrometer/OpenTelemetry.
- Add contract tests for payload evolution.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
