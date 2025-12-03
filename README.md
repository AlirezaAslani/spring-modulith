# Smart Parking System – Spring Modulith + Observability + OTEL

A production-grade **Spring Modulith** application demonstrating:

* Modular architecture (domain-based modules)
* REST API design
* H2 in-memory DB for local development
* Event-driven module communication
* Observability using **OpenTelemetry**, Micrometer & Actuator
* Local OTEL Collector (Docker) that exports telemetry to JSON
* Clean code, maintainable structure, and test-ready setup

---

## 📌 Architecture Overview

This project uses **Spring Modulith**, enabling:

* Clear domain boundaries
* Stable module APIs
* Internal/External eventing
* Enforced modularity via package rules
* Easy long-term maintenance

---

## 🚗 Features

### ✔ Parking Entry & Exit Management

* REST API for car entry/exit
* Event publishing between modules

### ✔ Billing & Calculation

* DDD-style services
* Uses Modulith event-driven architecture

### ✔ Observability (OTEL + Collector)

* Traces via `@WithSpan`
* Metrics via `@Timed`, `@Counted`
* Logs, metrics, traces exported to OTEL collector
* Local JSON file output for testing

### ✔ Local Development Ready

* H2 in-memory DB
* H2 Console enabled
* Actuator endpoints exposed

---

## 📦 Technologies Used

* **Java 17**
* **Spring Boot 3**
* **Spring Modulith**
* **Spring Web**
* **Spring Data JPA** (H2)
* **OpenTelemetry (OTEL)**
* **Micrometer + Actuator**
* **Docker Compose** (Collector)
* **Lombok**

---

## 🏛 Spring Modulith Configuration

The project uses Spring Modulith's event-based architecture.

### Key configuration:

```
spring:
  modulith:
    events:
      jdbc:
        schema: events
        schema-initialization:
          enabled: true
      republish-outstanding-events-on-restart: true
      completion-mode: update
```

### What This Means

* Events are stored in `events` schema
* On app restart, unsent events are republished
* You can choose **update | delete | archive** behavior for event completion
* Ideal for reliable communication between modules

---

## 📡 Observability (OTEL) Setup

OpenTelemetry provides logs, metrics, and traces in a unified format.
It helps identify performance issues and track requests end-to-end.

### OTel configuration from `application.yml`:

```
otel:
  instrumentation:
    micrometer:
      enabled: true
  service:
    name: "smartParking-service"
  exporter:
    otlp:
      endpoint: "http://localhost:4318"
      logging:
        enabled: true
      metrics:
        enabled: true
      traces:
        enabled: true
```

---

## 🐳 Running OTEL Collector (Local)

A lightweight OTEL Collector runs locally using Docker.

### docker-compose.yml

```
version: "3"
services:
  otel-collector:
    image: otel/opentelemetry-collector:0.120.0
    container_name: otel-collector
    restart: always
    user: "0:0"
    command: ["--config=/etc/otel-collector-config.yaml"]
    volumes:
      - ./otel-collector-config.yaml:/etc/otel-collector-config.yaml
      - ./telemetry-data.json:/etc/telemetry.json
    ports:
      - "4318:4318"
```

### Collector Behavior

* Receives OTLP telemetry on port **4318**
* Writes telemetry into **telemetry-data.json**
* Debug exporter prints raw data for visibility

---

## 🧪 Local Development (H2)

```
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

### H2 Console

Open browser:

```
http://localhost:9191/h2-console
```

Login:

```
JDBC URL: jdbc:h2:mem:testdb
User: sa
Password: (empty)
```

---

## 🚀 Running the Application

### Prerequisites

* Java 17+
* Docker installed

### Step 1 — Start OTEL Collector

```
docker compose up -d
```

### Step 2 — Run Spring Boot Application

```
./mvnw spring-boot:run
```

### Step 3 — Access Endpoints

Swagger UI:

```
http://localhost:9191/swagger-ui/index.html
```

Actuator endpoints:

```
http://localhost:8081/metrics
http://localhost:8081/health
```

---

## 🧠 Observability Examples

### Example Span

```
EntryController#entry
├─ DB Query
├─ Validation
└─ Response
```

### Example Metrics

* `latencyInSec.entry`
* `counter.entry`

---

## 📂 Project Structure

```
smart-parking-system
├─ allocation
├─ billing
├─ common
├─ config
├─ entry
├─ gateway
├─ notification
└─ SmartparkingSystemApplication

```

---

## 💡 Why Spring Modulith?

* Enforces **clean architecture**
* Ensures modules are independent
* Encourages clear boundaries
* Supports event-driven internal communication
* Reduces long-term technical debt

---

