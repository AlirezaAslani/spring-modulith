# 7. Verify module circular dependency violations

Spring Modulith checks for circular dependencies between modules and if found, reports them as violations.

# Why Circular Dependencies Are Bad

- They break the idea of independent modules
- They make testing difficult
- They make refactoring nearly impossible
- They prevent modules from being split into microservices later
- Spring Modulith detects them and either warns you or fails the build

## Simple Example of a Circular Dependency

### ❌ BAD EXAMPLE (Not Allowed)

**Module A: billing**

```text
billing
 └── spi
       └── BillingSPI
 ```

**Module B: reporting**
```text
textreporting
 └── spi
       └── ReportingSPI
 ```


Problem

billing uses ReportingSPI </br>
reporting uses BillingSPI

This creates a cycle: </br>
textbilling  →  reporting  </br>
reporting → billing


Here is your fully corrected, clean, and ready-to-use **Markdown (.md)** file content. Just copy everything below and save it as `avoid-circular-dependencies.md` (or any name you prefer).

```markdown
# Why Circular Dependencies Are Bad

- They break the idea of independent modules
- They make testing difficult
- They make refactoring nearly impossible
- They prevent modules from being split into microservices later
- Spring Modulith detects them and either warns you or fails the build

## Simple Example of a Circular Dependency

### ❌ BAD EXAMPLE (Not Allowed)

**Module A: billing**

```text
billing
 └── spi
       └── BillingSPI
```

**Module B: reporting**

```text
reporting
 └── spi
       └── ReportingSPI
```

#### Problem
- `billing` uses `ReportingSPI`
- `reporting` uses `BillingSPI`

This creates a cycle:

```text
billing  →  reporting  
reporting → billing
```

### ❌ BAD CODE (Circular Dependency Example)

**billing** module:

```java
// billing/spi/BillingSPI.java
public interface BillingSPI {
    void generateBill(String userId);
}
```

```java
// billing/internal/BillingManagement.java
@Service
public class BillingManagement implements BillingSPI {

    private final ReportingSPI reportingSPI; // depends on reporting

    public BillingManagement(ReportingSPI reportingSPI) {
        this.reportingSPI = reportingSPI;
    }

    @Override
    public void generateBill(String userId) {
        // ...
        reportingSPI.sendReport(userId);
    }
}
```

**reporting** module:

```java
// reporting/spi/ReportingSPI.java
public interface ReportingSPI {
    void sendReport(String userId);
}
```

```java
// reporting/internal/ReportingManagement.java
@Service
public class ReportingManagement implements ReportingSPI {

    private final BillingSPI billingSPI; // depends on billing

    public ReportingManagement(BillingSPI billingSPI) {
        this.billingSPI = billingSPI;
    }

    @Override
    public void sendReport(String userId) {
        billingSPI.generateBill(userId); // circular call
    }
}
```

### What Happens?

Spring Modulith will show:

```text
Detected circular module dependency:
billing -> reporting -> billing
```

Because each module requires the other to function.

## Correct Approach (Fixing the Cycle)

Break the cycle by introducing **domain events**.

### 1. billing publishes an event

```java
public record BillGeneratedEvent(String userId) {}
```

```java
@Service
public class BillingManagement implements BillingSPI {

    private final ApplicationEventPublisher publisher;

    public BillingManagement(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void generateBill(String userId) {
        // billing logic...
        publisher.publishEvent(new BillGeneratedEvent(userId));
    }
}
```

### 2. reporting listens to the event (no dependency on billing)

```java
@Component
public class ReportingListener {

    @EventListener
    public void handleBillGenerated(BillGeneratedEvent event) {
        System.out.println("Report sent for user " + event.userId());
    }
}
```

### Result: No Circular Dependency

```text
billing  → publishes event
reporting → listens to event
```

- `billing` does **not** depend on `reporting`
- `reporting` does **not** depend on `billing`
- Both modules are fully decoupled

## Summary

| Concept                | Explanation                                                                 |
|------------------------|-----------------------------------------------------------------------------|
| Circular dependency    | Two modules depend on each other → not allowed                              |
| Why it's bad           | Creates cycles, breaks modularity, complicates testing and refactoring     |
| How to fix             | Use domain events instead of direct SPI references                         |
| Spring Modulith tools  | Detects and warns (or fails) on circular dependencies                      |

---

[Previous: 5. Verify module boundary violations](step-5.md) &nbsp;&nbsp;&nbsp;&nbsp;
[Next: 7. Explicit module dependencies](step-7.md)

