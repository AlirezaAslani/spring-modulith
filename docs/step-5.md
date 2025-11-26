# 6. Verify module boundary violations

BillingService (an internal component of the billing module) is accessed by NotificationService.

Run `ModularityTest` and the test should FAIL with the following error:

```shell
- Module 'billing' depends on non-exposed type com.farabitech.smartparking_system.notification.internal.service.NotificationService within module 'notification'!
```

**Create (Allocation/Entity/Notification/...)Management.java and add the following code:**
</br>
</br>
Entry Management (Spring Modulith)</br>

This module provides the management façade for the entry bounded context inside a Spring Modulith-based system.
It implements the EntrySPI, which is the externally exposed API of the module.

EntryManagement acts as the public-facing entry point for other modules or external systems to interact with the entry module without exposing its internal services or domain model.


```java
package com.farabitech.smartparking_system.entry.internal;

import ...;

@Service
public class NotificationManagement implements NotificationSPI {

    private final NotificationService notificationService;
    private final ApplicationEventPublisher publisher;

    public EntryManagement(NotificationService notificationService, ApplicationEventPublisher publisher) {
        this.notificationService = notificationService;
        this.publisher = publisher;
    }

    @Override
    public void sendNotif(String text) {
        notificationService.sendNotif(text);
    }
    
}
```

**Why Management Class?**

## Management Layer in Spring Modulith

In **Spring Modulith**, each module contains **internal implementations** and **public interfaces (SPI)**.  
The **Management layer** serves several important roles:

---

### ✔️ Facade
The Management class acts as a **facade**, hiding internal services such as:

- `NotificationService`


and exposing only a **stable API** (e.g., `NotificationSPI`).

---

### ✔️ Boundary
It creates a **module boundary**:  
➡️ Other modules can interact **only** with the Management layer — **not** with internal classes.

---

### ✔️ Seam for Cross-Module Communication
The Management layer is the **safe entry point** for:

- Triggering domain logic
- Publishing domain events
- Enforcing invariants
- Adding validations & orchestrations

---

This design ensures **clean modularity**, **testability**, and **independence** between modules in a Spring Modulith system.


Now, in `BillingService` use `NotificationManagement` instead of `NotificationService` and use it to get the product details.

Run `ModularityTest`.

Test should PASS.


[Previous: 4. Understand NamedInterfaces](step-4.md) &nbsp;&nbsp;&nbsp;&nbsp;
[Next: 6. Verify module circular dependency violations](step-6.md)
