# Observability & Telemetry Annotations in Spring / Micrometer / OpenTelemetry

## What Is OpenTelemetry (OTel)? (Explained for People Who Know Nothing)

OpenTelemetry — usually called **OTel** — is a **standard system** that lets applications send:

* **Logs**
* **Metrics**
* **Traces**

to any monitoring backend (Grafana, Jaeger, Elastic, Prometheus...).

Think of OTel as the **universal language of observability**.

### 🔥 Why OTel Exists

Before OTel, every library used a different way to send metrics/traces.
OTel unified everything.

---

## 1. How OTel Works (Simple Explanation)

OTel works with **three parts**:

### 1️⃣ **Instrumentation / Agent**

This is the piece that automatically collects data from your application.
It can:

* Capture HTTP calls
* Capture DB calls
* Collect metrics (CPU, memory, JVM)
* Add spans for methods

### 2️⃣ **SDK (Inside App)**

This lives **inside your Java Spring Boot application**.
The SDK generates the telemetry data.

Your app → OTel SDK → sends telemetry to Collector.

### 3️⃣ **Collector (External Service)**

A standalone service (runs in Docker) that **receives telemetry from apps**.
It can:

* Filter
* Transform
* Batch
* Export to file, logs, Grafana, Prometheus, etc.

It gives you **full control over telemetry flow**.

**Without collector:**
Your app must talk directly to Prometheus/Jaeger/etc.
**With collector:**
Your app only talks to **one endpoint** → collector.

---

## 2. OTel Communication Flow (Easy Diagram)

```
Your Spring App
│  └─ OTel SDK (inside app)
│        └─ auto instrumentation, metrics & spans
│
▼
OTLP Protocol (HTTP 4318)

▼
OTel Collector (Docker)
│   ├─ Receivers
│   ├─ Processors
│   └─ Exporters
▼
Output: file, logs, Prometheus, Jaeger, Grafana
```

---

## 3. OTel Dependencies in Spring Boot (What You Need)

Spring Boot adds OTel through dependencies:

* `opentelemetry-sdk` → core SDK
* `opentelemetry-exporter-otlp` → sends data
* `opentelemetry-instrumentation-spring-boot` → auto instrumentation
* `micrometer-tracing-bridge-otel` → connects Micrometer to OTel

When enabled in `application.yml`, Spring automatically:

* Creates spans for HTTP controllers
* Creates spans for RestTemplate/WebClient
* Collects metrics
* Sends everything to the collector

---

## 4. Explaining Your Docker OTel Collector Config

This is the heart of your local observability environment.

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

### What This Means

* Runs OTel Collector version `0.120.0` in Docker
* Exposes port `4318` (OTLP HTTP port)
* Loads your config file
* Writes telemetry to a JSON file on your local machine

---

## 5. Breakdown of Collector Config

### Receivers

```
receivers:
  otlp:
    protocols:
      http:
        endpoint: 0.0.0.0:4318
```

Collector **listens for telemetry** from your app using OTLP/HTTP.

### Processors

```
processors:
  batch:
```

Collector batches telemetry to make export faster and cheaper.

### Exporters

```
exporters:
  file:
    path: /etc/telemetry.json
  debug:
    verbosity: detailed
```

* `file` → all telemetry goes to a JSON file you can inspect
* `debug` → prints raw telemetry to container logs

### Pipelines

```
service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [batch]
      exporters: [file]
```

A pipeline says:

1. take data from receiver
2. process it
3. export it to file

This is repeated for metrics & logs.

---

## 6. Explaining Your `application.yml` OTel Section

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

### What This Does

* Enables Micrometer–OTel bridge
* Sets service name (shows in traces)
* Sends traces, metrics, logs → OTel Collector at 4318
* Enables internal debug logging

So your app automatically exports telemetry to the collector.

---

## 7. How All Pieces Connect Together

```
Spring Boot App
│   ├─ Micrometer Metrics
│   ├─ Traces (@WithSpan)
│   ├─ Logs
│   └─ JVM metrics
│
▼ (OTLP HTTP)

OTel Collector (Docker)
│   ├─ stores all telemetry in telemetry-data.json
│   └─ prints debug info
```

This gives you a **complete local observability lab** using only:

* Docker
* Spring Boot
* OTel

--- & Telemetry Annotations in Spring / Micrometer / OpenTelemetry

This document provides a clear, senior-level explanation of the following annotations:

* `@Timed`
* `@Counted`
* `@WithSpan`
* `SpanKind` types

It also explains when to use them, best practices, and how they integrate into metrics/tracing pipelines.

---

## What Is a Span? (For People Who Know Nothing About Tracing)

A **span** is the basic building block of distributed tracing. If you are completely new, think of a span as:

### 📌 A "tracking box" around a piece of work

It measures:

* **How long** something takes
* **What happened** inside it
* **Whether it succeeded or failed**
* **What other spans it is connected to** (nested or across services)

### 📘 Simple Explanation

Imagine you are watching someone cook. Every action they do, you put a sticker on the table:

* "Chop vegetables"
* "Boil pasta"
* "Prepare sauce"

Each sticker has:

* Start time
* End time
* Details about the action

A **span** is exactly that sticker.

### 📌 How Systems Use Spans

A span helps systems understand:

* Where time is being spent
* Which steps are slow
* How a request moves between services (Service → Service → Database → Message Queue → etc.)

### 📌 Example Visualization

```
Request Started
│
├── Span 1: Validate Input (10ms)
│
├── Span 2: Call Payment Service (200ms)
│        ├── Span 2.1: Serialize JSON (2ms)
│        └── Span 2.2: HTTP Request (198ms)
│
└── Span 3: Save to Database (30ms)
Request Finished
```

### Summary

* A **span = one step of work**.
* A **trace = all spans combined for one request**.
* Tracing helps developers see what is slow and where failures happen.

---

## 1. `@Timed`

**Package:** `io.micrometer.core.annotation.Timed`

### Purpose

Measures how long a method or endpoint takes to execute. It records execution duration into Micrometer, exporting to backends such as Prometheus, OTLP Collector, Grafana, etc.

### Example

```java
@Timed(value = "latencyInSec.entry")
public Response entry() {
    ...
}
```

### What it Generates

* A **Timer** metric.
* Measures duration in **seconds (default)**.

### What You Learn

* Use for **performance measurement** of endpoints or logic.
* Useful for latency dashboards.
* Results appear as: `latencyInSec.entry_count`, `latencyInSec.entry_sum`, etc.

### Best Practices

* Use short, meaningful metric names.
* Use tags if your system has different paths or entity types.

---

## 2. `@Counted`

**Package:** `io.micrometer.core.annotation.Counted`

### Purpose

Counts how many times a method is invoked.

### Example

```java
@Counted(value = "counter.entry")
public Response entry() {
    ...
}
```

### What it Generates

* A **Counter** metric.
* Increments by 1 every time the method runs.

### What You Learn

* Good for tracking usage of endpoints.
* Use for alerting (e.g., sudden spike or drop).
* Appears in backend as: `counter.entry_total`.

### Best Practices

* Use counters for **event frequency**, not duration.

---

## 3. `@WithSpan`

**Package:** `io.opentelemetry.instrumentation.annotations.WithSpan`

### Purpose

Automatically creates a **span** for distributed tracing.

### Example

```java
@WithSpan(value = "EntryController#entry", kind = SpanKind.SERVER)
public Response entry() {
    ...
}
```

### What It Does

* Starts a span before method execution.
* Ends the span when the method finishes.
* Automatically associates the span with trace context.

### Why This Matters

* Allows you to see:

    * where time is spent
    * nested method calls
    * service-to-service latency breakdown
* Visible in Jaeger, Tempo, Zipkin, Elastic APM, etc.

### When to Use `@WithSpan`

* For internal **service logic** you want visible in tracing.
* When tracing is needed but you don't want manual `Span` code.

---

## 4. `SpanKind`

**Package:** `io.opentelemetry.api.trace.SpanKind`

SpanKind tells OpenTelemetry **what type of span** you are creating.

### Available Types

| Kind         | Meaning                                         | Use Cases                               |
| ------------ | ----------------------------------------------- | --------------------------------------- |
| **SERVER**   | Span is created by a server receiving a request | REST controllers, message listeners     |
| **CLIENT**   | Client making a request to another service      | WebClient, RestTemplate, Kafka producer |
| **PRODUCER** | Message being produced to a queue/topic         | Kafka/RabbitMQ send spans               |
| **CONSUMER** | Message being consumed                          | Kafka consumers, RabbitMQ consumers     |
| **INTERNAL** | Internal logic, default                         | Service methods, business logic         |

### Important Concepts

* **SERVER spans** mark the beginning of a trace in your service.
* **CLIENT spans** indicate outgoing calls.
* **INTERNAL spans** group sub-operations.
* **PRODUCER/CONSUMER** used for event-driven microservices.

You should always choose the correct kind to help tracing systems build accurate dependency graphs.

---

## 5. Combined Usage in a Controller

### Example

```java
@RestController
public class EntryController {

    @Timed(value = "latencyInSec.entry")
    @Counted(value = "counter.entry")
    @WithSpan(value = "EntryController#entry", kind = SpanKind.SERVER)
    @GetMapping("/entry")
    public String entry() {
        return "Hello!";
    }
}
```

### What Happens Internally

* Metrics System:

    * Timer measures response latency.
    * Counter measures number of requests.
* Tracing System:

    * Creates a server span and exports it.

### In Observability Backends

You will see:

* **Latency graph**
* **Call count graph**
* **Trace waterfall** for this endpoint

---

## 6. Best Practices & Tips

### 6.1 Metric Naming

Follow Micrometer guidelines:

* Use dot-separated names.
* Avoid uppercase.
* Avoid including IDs.

### 6.2 Tracing Best Practices

* Only add spans where it adds value.
* Avoid too many spans in loops.
* Use `INTERNAL` for internal logic.

### 6.3 Integration Tips

* OTLP exporter → OTEL Collector → Prometheus/Jaeger/Grafana.
* Make sure service name is set:

```
management.otlp.metrics.export.url=...
management.otlp.tracing.export.url=...
spring.application.name=...
```

---

## 7. Summary Diagram

```
┌─────────────────────────────┐
│ Incoming HTTP Request       │
└──────────────┬──────────────┘
               │
        @WithSpan (SERVER)
               │
    ┌──────────▼───────────┐
    │ Tracing: Create Span │
    └──────────┬───────────┘
               │
     @Timed and @Counted
               │
       ┌───────▼────────┐
       │ Metrics Export │
       └────────────────┘
```

