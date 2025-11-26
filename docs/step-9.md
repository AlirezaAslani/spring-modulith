# 10. Testing modules in isolation

Here is your fully polished, professional, and ready-to-use **Markdown (.md)** file — perfect for internal wikis, architecture docs, or onboarding guides.

Save it as `spring-modulith-complete-testing-guide.md`

```markdown
# Spring Modulith – Complete Testing Guide (All Scenarios & Features)

Spring Modulith gives you **first-class testing support** to guarantee:
- Modules are truly isolated
- Boundaries are respected
- Events are published & consumed correctly
- Architecture rules are enforced at test time
- Future microservice extraction is safe

Here are **all** the testing scenarios you’ll ever need.

## 1. Module Structure Validation
Ensures your modulith is architecturally sound (no cycles, correct dependencies, etc.)

```java
@ApplicationModuleTest
class ModuleStructureTests { }
```

Just having this empty test:
- Loads only the current module
- Validates allowed dependencies
- Checks that only SPI types are exposed
- Detects circular dependencies
- Fails fast if internal packages leak

## 2. Testing a Single Module (Fast, Isolated Context)

```java
@ApplicationModuleTest
class EntryModuleTests {

    @Autowired EntrySPI entrySPI;

    @Test
    void shouldRegisterEntry() {
        entrySPI.vehicleEntry("KA-01-HH-1234");
    }
}
```

Only the tested module + its direct dependencies are loaded.  
Perfect replacement for classic @SpringBootTest in modular apps.

## 3. STANDALONE Mode – True Modularity Check

```java
@ApplicationModuleTest(mode = ApplicationModuleTest.Mode.STANDALONE)
class StandaloneModuleTest {

    @OnlyInject ApplicationEventPublisher publisher;

    @Test
    void canRunWithoutSpringBoot() {
        publisher.publishEvent(new VehicleEnteredEvent("ABC"));
    }
}
```

Proves the module can run outside full Spring Boot → ideal for future microservice extraction.

## 4. Enforce SPI Boundaries (Only Public API Allowed)

```java
@ApplicationModuleTest
class ExposedApiVerificationTest {

    @Test
    void onlySpiTypesAreVisible(ApplicationModules modules) {
        modules.verify(); // fails if internal classes are used elsewhere
    }
}
```

Catches illegal access to `internal`, `impl`, etc.

## 5. Testing Event Publication

```java
@ApplicationModuleTest
class EventPublicationTest {

    @Test
    void shouldPublishVehicleEnteredEvent(ApplicationEvents events, EntrySPI spi) {

        spi.vehicleEntry("MH-04-9999");

        events.assertThat()
              .contains(VehicleEnteredEvent.class)
              .hasSize(1);
    }
}
```

## 6. Testing Event Listener Execution

```java
@ApplicationModuleTest
class EventListenerTest {

    @Test
    void notificationListenerShouldBeTriggered(ApplicationEvents events, EntrySPI spi) {

        spi.vehicleEntry("DL-10-AB-0001");

        events.ofType(NotificationSentEvent.class)
              .hasSize(1);
    }
}
```

## 7. Testing Event Chronology & Ordering

```java
@ApplicationModuleTest
class EventOrderTest {

    @Test
    void eventsShouldBeInCorrectOrder(ApplicationEvents events) {

        events.ofType(VehicleEnteredEvent.class)
              .chronologically()
              .allSatisfy(ev -> assertThat(ev.entryTime()).isNotNull());
    }
}
```

## 8. Integration Testing Across Multiple Modules

```java
@ApplicationModuleTest(modules = {"entry", "billing", "notification"})
class CrossModuleIntegrationTest {

    @Autowired EntrySPI entrySPI;

    @Test
    void entryShouldTriggerBillingAndNotification(ApplicationEvents events) {

        entrySPI.vehicleEntry("TS-09-XYZ");

        assertThat(events.ofType(BillGeneratedEvent.class)).hasSize(1);
        assertThat(events.ofType(NotificationSentEvent.class)).hasSize(1);
    }
}
```

Real modular integration testing — without starting the whole monolith.

## 9. Cycle Detection in Tests

```java
@ApplicationModuleTest
class NoCyclesTest {

    @Test
    void shouldHaveNoCircularDependencies(ApplicationModules modules) {
        modules.verify(); // fails with clear message if cycle exists
    }
}
```

## 10. JDBC Event Publication / Outbox Testing

```java
@ApplicationModuleTest
class OutboxPersistenceTest {

    @Autowired JdbcTemplate jdbc;

    @Test
    void eventShouldBeStoredInOutbox(EntrySPI spi) {

        spi.vehicleEntry("AP-39-1234");

        int count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM application_event WHERE event_type LIKE '%VehicleEnteredEvent'", Integer.class);

        assertThat(count).isOne();
    }
}
```

## 11. Full End-to-End Test (DDD Style)

```java
@SpringBootTest
@ApplicationModuleTest
class EndToEndFlowTest {

    @Autowired EntrySPI entrySPI;
    @Autowired NotificationRepository notificationRepo;

    @Test
    void completeUserJourney() {
        entrySPI.vehicleEntry("HR-26-DK-1234");

        assertThat(notificationRepo.findAll()).hasSize(1);
    }
}
```

## Summary Table – Which Test for What?

| Scenario                                    | Annotation / Setup                                      | Purpose                                      |
|---------------------------------------------|----------------------------------------------------------|----------------------------------------------|
| Basic structure & cycles                    | `@ApplicationModuleTest` (empty class)                  | Fast architectural validation               |
| Isolated module logic                       | `@ApplicationModuleTest`                                | Replace @SpringBootTest                      |
| Future microservice readiness               | `mode = STANDALONE`                                      | No hidden Spring Boot dependencies           |
| Enforce public API only                     | `modules.verify()`                                       | Prevent internal package leaks               |
| Event publication                           | `ApplicationEvents`                                      | Prove domain events are published            |
| Event consumption                           | `ApplicationEvents.ofType(...)`                          | Prove listeners run                          |
| Event ordering                              | `.chronologically()`                                     | Validate timeline correctness                |
| Cross-module integration                    | `modules = {"a", "b"}`                                   | Test real module interactions                |
| Outbox / JDBC events                        | Query `application_event` table                         | Verify reliable delivery                     |
| Full E2E                                    | `@SpringBootTest + @ApplicationModuleTest`              | Real-world flow with DB                      |

**Bottom line:**  
With Spring Modulith’s testing tools, you get **architecture enforcement as code** — your tests literally prevent architectural erosion.


[Previous: 8. Event Driven Communication](step-8.md) &nbsp;&nbsp;&nbsp;&nbsp;


