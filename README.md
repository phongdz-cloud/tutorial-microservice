# Spring Boot Microservices Demo

Modules:

- discovery-server (Eureka) on port 8761
- api-gateway (Spring Cloud Gateway) on port 8080
- user-service (REST + Eureka client) on port 8081

## Prerequisites

- Java 17+
- Maven 3.9+

## Build

```bash
mvn -q -DskipTests package
```

## Run Order

1. Discovery Server

```bash
mvn -pl discovery-server spring-boot:run
```

Visit `http://localhost:8761` to see the registry.

2. User Service

```bash
mvn -pl user-service spring-boot:run
```

3. API Gateway

```bash
mvn -pl api-gateway spring-boot:run
```

## Test

- Direct via gateway (service discovery):

  - `http://localhost:8080/users`
  - `http://localhost:8080/users/123`

- Direct to user-service (bypassing gateway):
  - `http://localhost:8081/users`

## Notes

- Gateway has a static route mapping `/users/**` to `lb://user-service` and also enables discovery locator for dynamic routes.
