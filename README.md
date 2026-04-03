This repository is intended for learning, experimentation, and reference purposes. It is not designed as a production-grade system.

# Spring Kafka Learning Lab

An educational Spring Boot project that demonstrates a complete publish and consume workflow using Kafka, with a local H2 mirror for easy inspection.

## Overview

This repository shows a simple but realistic learning flow:

1. A REST endpoint accepts a message payload.
2. The service publishes it to Kafka.
3. The same value is stored in H2 for easy querying.
4. A Kafka listener consumes records and logs metadata.

The project is intentionally compact so developers can understand each moving part quickly.

## What this repo demonstrates

- Spring Boot application layering: controller, service, stream, repository
- Kafka producer integration using `KafkaTemplate`
- Kafka consumer integration using `@KafkaListener`
- Request validation with Bean Validation
- Simple persistence with Spring Data JPA and H2
- Unit, web-layer, and integration tests (with embedded Kafka)

## Tech stack

- Java 17
- Spring Boot 2.7.x
- Spring Web
- Spring Kafka
- Spring Data JPA
- H2 in-memory database
- Gradle
- JUnit 5, Mockito, MockMvc, spring-kafka-test

## Project structure

```text
src/
  main/
    java/org/kafkalab/
      config/       Typed tutorial configuration
      controller/   REST API endpoints
      data/         Spring Data JPA repository interfaces
      model/        Request/response DTOs and JPA entity
      service/      Application workflow orchestration
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
```

## How to run locally

### Prerequisites

- Java 17
- Kafka broker reachable at `localhost:9092`

### Option A: Run Kafka with Docker (when virtualization is available)

```powershell
docker compose up -d
```

Stop later:

```powershell
docker compose down
```

### Option B: Run Kafka natively on Windows (recommended fallback)

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

3. Start Kafka broker:

```powershell
$env:KAFKA_HEAP_OPTS = "-Xmx1G -Xms1G"
& "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-server-start.bat" "C:\kfk\kafka_2.13-3.7.2\config\kraft\server.properties"
```

4. (Optional) Pre-create topic used by the app:

```powershell
& "C:\kfk\kafka_2.13-3.7.2\bin\windows\kafka-topics.bat" --bootstrap-server localhost:9092 --create --topic companies --partitions 1 --replication-factor 1
```

### Build the project

macOS/Linux:

```bash
./gradlew clean build
```

Windows PowerShell:

```powershell
.\gradlew.bat clean build
```

### Run the application

macOS/Linux:

```bash
./gradlew bootRun
```

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

If `8080` is occupied:

```powershell
.\gradlew.bat bootRun --args='--server.port=8081'
```

## How to run tests

macOS/Linux:

```bash
./gradlew test
```

Windows PowerShell:

```powershell
.\gradlew.bat clean test
```

Tests include unit tests, web-layer tests, and an embedded Kafka integration test.

## Example usage / API examples / flows

### Publish a message

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/messages" -ContentType "application/json" -Body '{"data":"hello kafka"}'
```

### Read stored messages

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/messages"
```

### End-to-end flow

1. POST message payload to `/api/messages`.
2. Service publishes to Kafka topic `companies`.
3. Service stores mirror record in H2.
4. Listener consumes record and logs partition/offset.
5. GET `/api/messages` returns stored records.

## Learning outcomes

After completing this repository, you should be able to:

- Explain how Spring Kafka producer and consumer components are wired
- Build a simple HTTP-to-Kafka publish workflow
- Keep code organized with clear controller/service/repository boundaries
- Use embedded Kafka tests for repeatable local verification
- Validate Kafka-backed behavior without production infrastructure complexity

## Limitations

- This project is intentionally not production-ready.
- No retries, dead-letter topics, schema registry, auth, or observability stack are included.
- H2 data is in-memory and resets on restart.
- Error handling is intentionally lightweight for teaching clarity.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
