# 2. Follow package-by-feature

**1. Refactor code to follow package-by-feature**

The existing code can be refactored to have the following structure:

```
smartparking
  |- config
  |- common
  |- vehicle
  |   - domain
  |   - web
  |- parking
  |   - domain
  |   - web
  |- billing
```

Create the directory structure using the following command:

```shell
$ cd src/main/java/com/farabitech/smartparking
$ mkdir -p {common/event,allocation/{config,controller,exceptions,model,...}}
```


```
smartparking
  |- allocation
    |   - config
    |   - controller
    |   - exceptions
    |   - model
    |   - repository
    |   - service
    |   - spi
        |   - exceptions
        |   - AllocationSPI.java
        
  |- billing
    |   - config
    |   - controller
    |   - exceptions
    |   - model
    |   - repository
    |   - service
    |   - spi
  |- common
      - event
            - VehicleEnteredEvent.java
            - VehicleExitedEvent.java
  |- config
  |- entry
    |   - config
    |   - controller
    |   - exceptions
    |   - model
    |   - repository
    |   - service
    |   - spi
  |- notification
    |   - config
    |   - controller
    |   - exceptions
    |   - model
    |   - repository
    |   - service
    |   - spi
  |- reporting
    |   - config
    |   - controller
    |   - exceptions
    |   - model
    |   - repository
    |   - service
    |   - spi
  
```

2. Split **GlobalExceptionHandler.java** into separate exception handlers for each module. 

**Create CatalogExceptionHandler.java**

```java
package com.farabitech.smartparking;

@RestControllerAdvice
class CatalogExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handle(ProductNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Product Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
```

**Create OrdersExceptionHandler.java**

```java
package com.farabitech.smartparking;

@RestControllerAdvice
class OrdersExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail handle(OrderNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Order Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(InvalidOrderException.class)
    ProblemDetail handle(InvalidOrderException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid Order Creation Request");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
```

**3. Accordingly, move tests to the new package structure**

[Previous: 1. Understand existing code](step-1.md) &nbsp;&nbsp;&nbsp;&nbsp;
[Next: 3. Add Spring Modulith support](step-3.md)
