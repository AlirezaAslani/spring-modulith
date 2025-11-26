# 9. Event Driven Communication

Prefer event-driven communication between modules.

## Using Spring's ApplicationEventPublisher

```java
@Service
class OrderService {
    private final ApplicationEventPublisher publisher;

    void createOrder() {
        OrderCreatedEvent event = new OrderCreatedEvent(...);
        publisher.publishEvent(event);
    }
}

@Component
class OrderCreatedEventHandler {
    @EventListener
    void handle(OrderCreatedEvent event) {
        log.info("Received order created event: {}", event);
    }
}
```

## Using TransactionalEventListener

```java
package com.farabitech.smartparking_system.entry.internal.service;

import com.farabitech.smartparking_system.entry.spi.event.VehicleEnteredEvent;
import com.farabitech.smartparking_system.entry.internal.model.ParkingEntry;
import com.farabitech.smartparking_system.entry.internal.repository.ParkingEntryRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EntryService {

    // save vehicle entry details to DB
    // allocate a parking slot
    // send notification to the user

    private final ParkingEntryRepository repository;
    private final ApplicationEventPublisher publisher;

    public EntryService(ParkingEntryRepository repository,
                        ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }


    @Transactional
    public void vehicleEntry(String vehicleNumber) {
        ParkingEntry parkingEntry = new ParkingEntry(null, vehicleNumber, LocalDateTime.now(), null, true);
        repository.save(parkingEntry);
        // publish an event
        publisher.publishEvent(new VehicleEnteredEvent(vehicleNumber, parkingEntry.getEntryTime()));

    }
}


```


## Using Spring Modulith Event Publishing

Add the following dependency to your project.

```xml
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-events-api</artifactId>
</dependency>
```

Use the `@ApplicationModuleListener` annotation to register event handlers instead of `@EventListener`.

```java
@Service
public class NotificationEventListener {

    @ApplicationModuleListener
    public void notifyOnVehicleEntry(VehicleEnteredEvent event) {
        // Logic to send notification to the user
        System.out.println("📩 Notification: Vehicle " + event.vehicleNumber() +
                " entered at " + event.entryTime() + ". Welcome!");
    }
}
```


## The Event Publication Registry
The events can be persisted in a database so that they can be processed without losing then on application failures.

```xml
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-starter-jdbc</artifactId>
</dependency>
```

```properties
spring.modulith.events.jdbc.schema-initialization.enabled=true
# completion-mode options: update | delete | archive
spring.modulith.events.completion-mode=update
spring.modulith.events.republish-outstanding-events-on-restart=true
```

Start the application, create an order and check the data in `event_publication` table.

## Externalizing Events
The events can also be published to an external messaging system like Kafka or RabbitMQ.

```xml
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-events-amqp</artifactId>
</dependency>
```

Here is your fully polished, ready-to-use **Markdown (.md)** file content.  
Copy and save it as `application-module-listener-vs-event-listener.md` (or any name you prefer).

```markdown
# @ApplicationModuleListener vs @EventListener vs @TransactionalEventListener

Clear, side-by-side comparison so you never pick the wrong one again.

## 1. @ApplicationModuleListener (Spring Modulith)

**Use when:**  
You are building a **Spring Modulith** application and want **module-to-module** event communication that respects boundaries, visibility, and transactional correctness.

### Key Characteristics
- Designed specifically for Spring Modulith
- Enforces module isolation and SPI exposure rules
- Automatically behaves like `@TransactionalEventListener(phase = AFTER_COMMIT)`
- Works seamlessly with Modulith’s JDBC-based event publication (outbox pattern)
- Validation at build/startup time if used with `@ApplicationModule`

### Example
```java
@ApplicationModuleListener
void handle(VehicleEnteredEvent event) {
    System.out.println("Modulith event received: " + event.vehicleNumber());
}
```

### Feature Support
| Feature                                 | Support |
|-----------------------------------------|---------|
| Modulith module boundary enforcement    | Yes     |
| Executes only after transaction commit  | Yes (automatically) |
| Works with JDBC event publication/outbox| Yes     |
| SPI exposure validation                 | Yes     |
| Fires if transaction rolls back         | No      |

## 2. @EventListener (Spring Core)

**Use when:**  
You need a simple, fire-and-forget listener **without** transactional guarantees.

### Key Characteristics
- Plain Spring listener
- Executes **immediately** when the event is published
- **Danger:** Event fires even if the transaction later rolls back → inconsistent state!

### Example
```java
@EventListener
public void handle(VehicleEnteredEvent event) {
    System.out.println("Received event: " + event.vehicleNumber());
}
```

### Dangerous Example
```java
@Transactional
public void saveAndPublish() {
    repo.save(entity);
    publisher.publishEvent(new VehicleEnteredEvent(...));
    throw new RuntimeException(); // ← Event was already sent!
}
```

### Feature Support
| Feature                                 | Support |
|-----------------------------------------|---------|
| Executes after commit                   | No      |
| Safe with transaction rollback          | No      |
| Modulith module boundaries              | No      |
| Event persisted to DB (outbox)          | No      |

## 3. @TransactionalEventListener (Spring Core)

**Use when:**  
You need **after-commit** semantics but are **not** using Spring Modulith (or don’t need Modulith’s extra checks).

### Key Characteristics
- Buffers events during the transaction
- Fires them only after successful commit
- No Modulith awareness

### Example
```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleAfterCommit(VehicleEnteredEvent event) {
    System.out.println("After-commit: " + event.vehicleNumber());
}
```

### Feature Support
| Feature                                 | Support |
|-----------------------------------------|---------|
| Executes only after commit              | Yes     |
| Buffers events during transaction       | Yes     |
| Modulith module boundaries              | No      |
| Works with JDBC outbox                  | No      |

## Side-by-Side Comparison Table

| Feature                                      | @ApplicationModuleListener | @EventListener | @TransactionalEventListener |
|----------------------------------------------|----------------------------|----------------|------------------------------|
| Spring Modulith aware                        | Yes                        | No             | No                           |
| Enforces module boundaries & SPI visibility  | Yes                        | No             | No                           |
| Executes only after transaction commit       | Yes (automatic)            | No             | Yes (with `AFTER_COMMIT`)    |
| Fires when transaction rolls back            | No                         | Yes            | No                           |
| Works with Modulith JDBC Event Publication  | Yes                        | No             | No                           |
| Part of reliable outbox pattern              | Yes                        | No             | No                           |
| Best for cross-module communication          | Best choice                | Not suitable   | Not suitable                 |

## Recommendation for a Spring Modulith Project

| Use Case                                    | Recommended Annotation            |
|---------------------------------------------|-----------------------------------|
| Module-to-module communication              | `@ApplicationModuleListener`     |
| Enforce architecture boundaries            | `@ApplicationModuleListener`     |
| Need reliable after-commit delivery        | `@ApplicationModuleListener`     |
| Simple local events (same module, no tx)    | `@EventListener`                  |
| Want after-commit but not using Modulith    | `@TransactionalEventListener`    |

**Rule of thumb in a Modulith project:**  
→ **Always prefer `@ApplicationModuleListener`** for any event that crosses module boundaries.

## Final Summary Table

| Scenario                                      | Best Choice                          |
|-----------------------------------------------|--------------------------------------|
| Modulith + cross-module events                | `@ApplicationModuleListener`         |
| Maintain clean architecture boundaries       | `@ApplicationModuleListener`         |
| Need reliable delivery (outbox)               | `@ApplicationModuleListener`         |
| Simple internal same-module events            | `@EventListener`                     |
| After-commit needed, but no Modulith          | `@TransactionalEventListener`       |

---


Here is your fully polished, ready-to-use **Markdown (.md)** file.  
Copy and save it as `modulith-events-require-transactional.md`

```markdown
# Spring Modulith Events: Why @Transactional Is Mandatory for Reliable Publication

If you want Spring Modulith to **persist events to the database** and **publish them only after commit** (outbox pattern), you **must** annotate the publishing method with `@Transactional`.

Otherwise the event is published immediately — or worse, not persisted at all.

## The Two Types of Events in Spring Modulith

| Type                                      | How you publish                     | Requires @Transactional? | Persisted to DB? | Published when?          | Risk of inconsistency? |
|-------------------------------------------|-------------------------------------|---------------------------|------------------|---------------------------|-------------------------|
| Regular Spring Events                     | `publisher.publishEvent(event)`     | No                        | No               | Immediately               | Yes (fires even on rollback) |
| Modulith Transactional Events (reliable) | `publisher.publishEvent(event)`     | Yes                       | Yes (outbox)     | After successful commit   | No                      |

## Why @Transactional Is Required for Reliable Events

Spring Modulith uses Spring’s **TransactionSynchronizationManager** to:

1. Buffer published events during an active transaction  
2. Insert them into the `application_event` table (if JDBC event publication is enabled)  
3. Publish them **only after** the transaction commits successfully  

→ If there is **no active transaction**, Modulith has nothing to hook into → **no buffering, no persistence, no reliable delivery**.

## Correct Example — Event Is Persisted & Published Safely

```java
@Service
class EntryService {

    private final ApplicationEventPublisher publisher;
    private final ParkingEntryRepository repository;

    public EntryService(ApplicationEventPublisher publisher,
                        ParkingEntryRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    @Transactional
    public void registerEntry(String plate) {
        ParkingEntry entry = new ParkingEntry(plate, LocalDateTime.now());
        repository.save(entry);

        // This event will be:
        // • stored in application_event table
        // • published only after commit
        publisher.publishEvent(new VehicleEnteredEvent(plate, entry.getEntryTime()));
    }
}
```

## Incorrect Example — Event Is Lost or Published Too Early

```java
// Missing @Transactional → Modulith cannot intercept the transaction
public void registerEntry(String plate) {
    repository.save(...);
    publisher.publishEvent(new VehicleEnteredEvent(...)); // NOT persisted!
}
```

Consequences:
- Event is **not** inserted into the outbox table
- Event is **not** retried by the EventPublicationRegistry
- If you rely on external listeners → they may never receive it

## Summary Table

| Scenario                                    | @Transactional Required? | Event Stored in DB? | Published When?         | Safe from Rollback? |
|---------------------------------------------|---------------------------|---------------------|--------------------------|---------------------|
| Regular Spring `@EventListener`             | No                        | No                  | Immediately              | No                  |
| Modulith transactional events              | Yes                       | Yes                 | After commit             | Yes                 |
| JDBC-based event publication (outbox)       | Yes                       | Yes                 | After commit             | Yes                 |
| Reliable cross-module communication         | Yes                       | Yes                 | After commit             | Yes                 |

## Final Answer

> **In a Spring Modulith project, always annotate methods that publish cross-module events with `@Transactional` if you want:**
> - Events persisted to the database
> - Outbox pattern / reliable delivery
> - Publication only after successful commit
> - No inconsistent state on rollback

> If you skip `@Transactional`, you fall back to plain Spring immediate events — **not recommended** for any real-world modulith application.




[Previous: 7. Explicit module dependencies](step-7.md) &nbsp;&nbsp;&nbsp;&nbsp;
[Next: 9. Testing modules in isolation](step-9.md)
