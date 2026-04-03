This repository is intended for learning, experimentation, and reference purposes. It is not designed as a production-grade system.

# Kafka Learning Lab

Professional learning/reference project for understanding REST-to-Kafka data flow, service-layer design, and testable Spring Boot architecture using kafka-learning-lab conventions.

## Overview

### What this project does

This project exposes a REST API that accepts a message payload, publishes it to Kafka, stores a mirror copy in H2, and allows retrieval of stored records through a read endpoint.

### Why this problem is relevant in real-world systems

Many production systems use asynchronous messaging to decouple services and improve resilience. Understanding how an API request becomes a durable event is a core engineering skill for event-driven architectures.

## Real-World Context

### Where this concept is used

- Event-driven microservices that publish business events (order-created, payment-received, shipment-dispatched)
- CDC/event pipelines where upstream systems expose domain changes through Kafka
- Analytics ingestion where operational APIs emit events consumed by downstream processors

### Example use cases

- Ecommerce checkout service publishes order events for inventory, billing, and notifications
- Fintech transaction API publishes audit/compliance events to independent consumers
- Platform teams centralize event streams to power near-real-time dashboards

## What This Repo Demonstrates

- Clean layered architecture: controller, service, stream, repository, model
- Synchronous request handling combined with asynchronous event publishing
- Kafka producer and consumer setup in Spring Boot
- Typed configuration and validation at API boundaries
- Local persistence for observability during learning
- Unit, web-layer, and integration testing patterns

## Architecture / Flow

### Component responsibilities

- MessageController: handles HTTP requests and validation boundary
- MessageService: orchestrates publish and persist workflow
- KafkaMessageProducer: encapsulates KafkaTemplate send behavior
- KafkaMessageConsumer: demonstrates consumer-group-based message handling
- KafkaMessageRepository: provides deterministic read ordering from H2

### Request lifecycle

1. Client sends POST to /api/messages
2. Controller validates payload
3. Service publishes payload to the configured Kafka topic (default: `companies`, set via `learning-lab.kafka.topic-name`)
4. Service stores the same payload in H2
5. Consumer receives event asynchronously and logs metadata
6. Client can GET /api/messages to inspect stored records

### Flow diagram

Client
  |
  v
MessageController
  |
  v
MessageService
  |------------------> KafkaMessageProducer ----> Kafka Topic (configurable via learning-lab.kafka.topic-name, default: companies)
  |
  v
KafkaMessageRepository ----> H2 (in-memory)
  |
  v
GET /api/messages response

KafkaMessageConsumer <---- Kafka Topic (configurable via learning-lab.kafka.topic-name, default: companies)

### Design and trade-off notes

- Publish-then-persist order keeps the learning flow explicit and easy to reason about
- REST entrypoint is synchronous for simplicity; event consumption is asynchronous
- In-memory H2 improves local feedback speed but does not preserve data across restarts
- Consumer behavior is intentionally log-focused rather than side-effect heavy to keep concepts clear

### Scalability considerations (simplified)

- Kafka partitions allow horizontal consumer scaling in real systems
- Consumer group semantics prevent duplicate processing inside one group
- For production, this flow would add retries, dead-letter handling, and idempotency controls

## Tech Stack

- Java 17
- Spring Boot 2.7.x
- Spring Web
- Spring Kafka
- Spring Data JPA
- H2 in-memory database
- Gradle
- JUnit 5, Mockito, MockMvc, spring-kafka-test

## Project Structure

src/
  main/
    java/org/kafkalab/
      config/       Typed tutorial configuration
      controller/   REST API endpoints
      data/         Spring Data repository interfaces
      model/        Request/response DTOs and JPA entity
      service/      Business workflow orchestration
      stream/       Kafka producer and consumer components
    resources/
      application.yml
  test/
    java/org/kafkalab/
      controller/   Web layer tests
      service/      Service unit tests
      stream/       Producer unit tests
      MessageFlowIntegrationTest.java
build.gradle
settings.gradle
compose.yml
README.md
LICENSE

## How to Run Locally

### Prerequisites

- Java 17
- Kafka reachable on localhost:9092

### Option A: Start Kafka with Docker (if virtualization is available)

```powershell
docker compose up -d
docker compose ps
```

Stop later:

```powershell
docker compose down
```

### Optional: Advanced Setup for Native Kafka on Windows

1. Download and extract Kafka:

```powershell
$root = "C:\kfk"
New-Item -ItemType Directory -Path $root -Force | Out-Null
curl.exe -k -L "https://archive.apache.org/dist/kafka/3.7.2/kafka_2.13-3.7.2.tgz" -o "$root\kafka_2.13-3.7.2.tgz"
tar -xzf "$root\kafka_2.13-3.7.2.tgz" -C $root
```

2. Initialize KRaft metadata (one-time):

```powershell
$clusterId = & "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-storage.bat" random-uuid
& "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-storage.bat" format -t $clusterId.Trim() -c "C:\kfk\kafka_2.13-3.7.2\config\kraft\server.properties"
```

3. Start broker:

```powershell
$env:KAFKA_HEAP_OPTS = "-Xmx1G -Xms1G"
& "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-server-start.bat" "C:\kfk\kafka_2.13-3.7.2\config\kraft\server.properties"
```

4. Optional topic creation:

```powershell
& "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-topics.bat" --bootstrap-server localhost:9092 --create --topic companies --partitions 1 --replication-factor 1
```

### Build

- macOS/Linux: ./gradlew clean build
- Windows: .\gradlew.bat clean build

### Run application

- macOS/Linux: ./gradlew bootRun
- Windows: .\gradlew.bat bootRun

If port 8080 is occupied:

- .\gradlew.bat bootRun --args='--server.port=8081'

## How to Run Tests

- macOS/Linux: ./gradlew test
- Windows: .\gradlew.bat clean test

Test suite includes:

- Unit tests for service and producer logic
- Web-layer tests for controller behavior
- Embedded-Kafka integration test for end-to-end wiring

## Example Usage

### Publish message

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/messages" -ContentType "application/json" -Body '{"data":"hello kafka"}'
```

### Read messages

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/messages"
```

### Suggested verification sequence

1. Start Kafka
2. Run app
3. POST one message
4. GET messages and confirm persisted output

## Learning Outcomes

By completing this lab, a developer should be able to:

- Explain API-to-event data flow in a layered Spring application
- Implement a basic producer/consumer workflow with Kafka
- Structure code around clear component responsibilities
- Write tests at unit, web, and integration levels
- Discuss trade-offs between synchronous APIs and asynchronous messaging

## Limitations

- This project is not production-ready by design
- No schema registry, auth, retries, DLQ, observability stack, or outbox pattern
- Local H2 storage is ephemeral
- Error handling and operational concerns are intentionally simplified

## Future Improvements

- Add structured error responses and centralized exception handling
- Add outbox pattern variant for stronger consistency guarantees
- Add idempotent consumer example and retry/DLQ demonstration
- Add metrics and tracing examples (Micrometer/OpenTelemetry)
- Add contract tests for event payload evolution

## License
This project is licensed under the MIT License - see the LICENSE file for details.
