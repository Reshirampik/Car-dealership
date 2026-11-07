# Car Dealership

<p align="center">
  <strong>A backend platform for managing car dealership operations.</strong>
  <br />
  Built with Java 21, Spring Boot, PostgreSQL, RabbitMQ, gRPC and Keycloak.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?logo=springboot" alt="Spring Boot 3.2.5" />
  <img src="https://img.shields.io/badge/Gradle-8.12-02303A?logo=gradle" alt="Gradle 8.12" />
  <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?logo=postgresql" alt="PostgreSQL 15" />
  <img src="https://img.shields.io/badge/RabbitMQ-3-FF6600?logo=rabbitmq" alt="RabbitMQ" />
  <img src="https://img.shields.io/badge/gRPC-Protocol%20Buffers-244C5A?logo=grpc" alt="gRPC" />
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker" alt="Docker Compose" />
  <img src="https://img.shields.io/badge/Keycloak-OAuth2-4D4D4D?logo=keycloak" alt="Keycloak" />
</p>

---

## Overview

**Car Dealership** is a backend application for a car dealership domain. It separates order processing and warehouse operations into two Spring Boot services that communicate through synchronous gRPC calls and asynchronous RabbitMQ messages.

The project focuses on backend architecture and service integration: REST APIs, persistence, authentication, inter-service communication, containerized infrastructure and automated tests.

## Architecture

The application consists of two services built from the same Gradle project and started with different Spring profiles:

- **Order Service** — handles the order-facing part of the application and exposes the primary REST API.
- **Storage Service** — handles storage-related operations and provides both a REST API and a gRPC endpoint for internal communication.

Each service uses its own PostgreSQL database. Keycloak is used as the identity provider, while RabbitMQ carries asynchronous events between the services.

```text
                       ┌───────────────────┐
                       │      Keycloak     │
                       │   Authentication  │
                       └─────────┬─────────┘
                                 │ JWT
                                 │
┌────────────┐   REST    ┌────────▼────────┐       gRPC       ┌─────────────────┐
│   Client   │──────────▶│  Order Service  │────────────────▶│  Storage Service│
└────────────┘           │     :8080       │                 │   :8082 / :9090 │
                         └────────┬────────┘                 └────────┬────────┘
                                  │                                   │
                                  │       RabbitMQ events             │
                                  └───────────────┬───────────────────┘
                                                  │
                                         ┌────────▼────────┐
                                         │    RabbitMQ     │
                                         └─────────────────┘

                         ┌────────────────┐       ┌────────────────┐
                         │  Order database│       │ Storage database│
                         │   PostgreSQL   │       │   PostgreSQL    │
                         └────────────────┘       └────────────────┘
```

### Communication

| Method | Direction | Purpose |
| --- | --- | --- |
| REST | Client → Order Service / Storage Service | External API access |
| gRPC | Order Service → Storage Service | Synchronous internal requests |
| RabbitMQ | Order Service ↔ Storage Service | Asynchronous business events |
| OAuth2 / JWT | Client → Services | Authentication and access protection |

RabbitMQ exchange and routing keys are configurable through application properties. The application also exposes a configurable interval for publishing queued Outbox events.

## Main capabilities

- Separate order-processing and storage responsibilities.
- REST APIs for interacting with the services.
- Synchronous communication between services using gRPC and Protocol Buffers.
- Asynchronous event exchange through RabbitMQ.
- Independent PostgreSQL databases for service data.
- OAuth2 Resource Server integration with Keycloak and JWT.
- API documentation through Swagger UI / OpenAPI.
- Unit and integration testing, including Testcontainers for PostgreSQL and RabbitMQ.

## Tech Stack

| Category | Technologies |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Build | Gradle 8.12 |
| REST API | Spring Web, Spring Validation |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL 15 |
| Security | Spring Security, OAuth2 Resource Server, Keycloak |
| Messaging | Spring AMQP, RabbitMQ |
| Service communication | gRPC, Protocol Buffers |
| API documentation | Springdoc OpenAPI / Swagger UI |
| Testing | JUnit 5, Spring Boot Test, H2, Testcontainers |
| Code style | Spotless, Google Java Format |
| Infrastructure | Docker, Docker Compose |

## Getting Started

### Prerequisites

- Docker Engine or Docker Desktop
- Docker Compose v2

The Docker Compose setup builds and starts both application services together with PostgreSQL, RabbitMQ and Keycloak.

### Run the application

```bash
git clone https://github.com/Reshirampik/car-dealership.git
cd car-dealership
docker compose up --build
```

The first startup may take a little longer because Docker builds the application image and downloads the required dependencies.

To stop the containers:

```bash
docker compose down
```

To stop the containers and remove the database volumes as well:

```bash
docker compose down -v
```

> **Note:** The credentials currently defined in `docker-compose.yml` are development defaults. Do not reuse them in a production environment.

## Local Services

| Component | Address | Description |
| --- | --- | --- |
| Order Service | `http://localhost:8080` | Main REST API |
| Order Service Swagger UI | `http://localhost:8080/swagger-ui.html` | Interactive API documentation |
| Storage Service | `http://localhost:8082` | Storage REST API |
| Storage Service Swagger UI | `http://localhost:8082/swagger-ui.html` | Interactive API documentation |
| Storage Service gRPC | `localhost:9090` | Internal gRPC endpoint |
| Keycloak | `http://localhost:8081` | Identity provider / admin console |
| RabbitMQ AMQP | `localhost:5672` | Message broker |
| RabbitMQ Management | `http://localhost:15672` | Broker management UI |
| Order PostgreSQL | `localhost:15432` | Order Service database |
| Storage PostgreSQL | `localhost:15433` | Storage Service database |

The Keycloak container is configured with development admin credentials in `docker-compose.yml`. Change these credentials before using the setup beyond local development.

## Build and Tests

The Gradle Wrapper is included, so a separate Gradle installation is not required. Use JDK 21 when running Gradle tasks locally.

Build the application:

```bash
./gradlew clean assemble
```

Run unit tests:

```bash
./gradlew test
```

Run integration tests:

```bash
./gradlew integrationTest
```

Run the complete verification lifecycle:

```bash
./gradlew check
```

Integration tests use Testcontainers, so Docker must be available while they are running. The `check` task is configured to run the integration tests as part of verification.

### Code formatting

Apply the configured Google Java Format rules:

```bash
./gradlew spotlessApply
```

Check formatting without modifying files:

```bash
./gradlew spotlessCheck
```

## Project Structure

```text
.
├── .github/
│   └── workflows/          # GitHub Actions workflow
├── docker/
│   ├── Dockerfile           # Multi-stage Java 21 image build
│   └── keycloak/            # Keycloak import/configuration files
├── gradle/
│   └── wrapper/             # Gradle Wrapper configuration
├── src/
│   ├── main/
│   │   ├── java/            # Application source code
│   │   ├── proto/           # Protocol Buffers definitions
│   │   └── resources/       # Spring configuration and resources
│   └── integrationTest/     # Integration tests
├── build.gradle
├── docker-compose.yml
├── gradlew
└── settings.gradle
```

## Project Notes

- The two services are launched from the same application artifact using the `order-service` and `storage-service` Spring profiles.
- Service ports, database connection details, RabbitMQ settings, JWT issuer and gRPC target can be configured through environment variables.
- The Docker Compose configuration is intended for local development and demonstration.

---

<p align="center">
  Built as a backend engineering project with Java and Spring Boot.
</p>
