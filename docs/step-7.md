# 8. Explicit module dependencies
Note: it's not part of this project just this is a simple sample 

```markdown
# Restricting Module Dependencies with Spring Modulith  
Example: The **orders** module may depend **only** on **catalog**

Assume we have the following module structure:

```text
bookstore
 ├── catalog
 └── orders
```

**Goal:**  
The `orders` module is allowed to depend **only** on the `catalog` module.  
It must **not** depend on any other module (e.g., `customers`, `payments`, `shipping`, etc.).

## Step 1 — Declare Allowed Dependencies

Create a `package-info.java` file at the root of the `orders` module:

**Location:**
```text
src/main/java/com/sample/bookstore/orders/package-info.java
```

**Content:**
```java
@ApplicationModule(allowedDependencies = {"catalog"})
package com.sample.bookstore.orders;

import org.springframework.modulith.ApplicationModule;
```

### What this means
- The `orders` module is now **explicitly restricted**.
- It may only depend on the module named `catalog`.
- Any attempt to use a type from another module will be detected as a violation.

## Important Notes About `allowedDependencies`

- You specify **module names**, **not** package names.
- The module name is the top-level package segment after the base package.

  | Full package                            | Module name |
    |-----------------------------------------|-------------|
  | `com.sample.bookstore.catalog`        | `catalog`   |
  | `com.sample.bookstore.orders`         | `orders`    |
  | `com.sample.bookstore.payments`       | `payments`  |

## Step 2 — Test the Restriction

Add (or imagine) code inside the `orders` module that references another module:

```java
// Example inside OrderRestController.java or any class in orders
@GetMapping("/test")
PagedResult<OrderView> getOrdersPaged() {
    // This would cause a violation:
    // return customerService.getCustomerOrders(); // customer module not allowed!
    return orderService.findAll();
}
```

If a class from a **non-allowed** module (e.g., `customer`, `payment`, `shipping`) is referenced, Spring Modulith will flag it.

## Step 3 — Run Module Verification

Execute one of the following:

```bash
mvn spring-modulith:verify
```

or simply start the application (if strict verification is enabled).

### What you will see on violation

```text
Module 'orders' depends on non-exposed type 
'com.sample.bookstore.customer.Customer' from module 'customer'
```

or

```text
Module 'orders' is not allowed to depend on module 'customer' 
(allowed dependencies: [catalog])
```

## Summary Table

| Feature                   | Behavior                                                                 |
|---------------------------|--------------------------------------------------------------------------|
| `@ApplicationModule`      | Marks a package as a Spring Modulith module                              |
| `allowedDependencies`    | Restricts which other modules this module may depend on                  |
| Violation detection       | Automatically detected by `spring-modulith:verify` or at application startup |
| Purpose                   | Enforces clean, independent modules and respects architectural boundaries |

**Result:** Your modules stay clean, decoupled, and follow the intended dependency rules from day one.

---


[Previous: 6. Verify module circular dependency violations](step-6.md) &nbsp;&nbsp;&nbsp;&nbsp;
[Next: 8. Event Driven Communication](step-8.md)
