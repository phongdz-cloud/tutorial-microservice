# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Spring Boot microservices tutorial** demonstrating enterprise-grade patterns including service discovery, API gateway, authentication, caching, and monitoring. The project uses a multi-module Maven structure with 5 microservices and shared libraries.

**Architecture**: Service mesh with Eureka discovery, Spring Cloud Gateway, JWT authentication, Redis caching, and Kubernetes deployment.

## Development Commands

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker & Docker Compose

### Local Development Setup

1. **Start databases first:**
```bash
docker compose -f docker-compose.db.yml up -d
```

2. **Start services in order:**
```bash
# 1. Discovery Server (Eureka) - port 8761
mvn -pl discovery-server spring-boot:run

# 2. User Service - port 8081
mvn -pl user-service spring-boot:run

# 3. Auth Service - port 8082
mvn -pl auth-service spring-boot:run

# 4. API Gateway - port 8080
mvn -pl api-gateway spring-boot:run
```

### Build & Test Commands

```bash
# Clean build all modules
mvn clean package -DskipTests

# Build with tests
mvn clean package

# Run tests for specific module
mvn -pl user-service test

# Run specific test class
mvn -pl user-service test -Dtest=UserServiceImplTest

# Run integration tests
mvn -pl user-service test -Dtest=*IntegrationTest

# Build single module
mvn -pl auth-service clean package -DskipTests
```

### Monitoring & Health Checks

```bash
# Service registry (Eureka)
http://localhost:8761

# API Gateway health
http://localhost:8080/actuator/health

# User Service metrics
http://localhost:8081/actuator/metrics

# Swagger UI (via Gateway)
http://localhost:8080/swagger-ui.html
```

## Architecture & Key Patterns

### Service Communication
- **Service Discovery**: Eureka Server (`discovery-server:8761`)
- **API Gateway**: Spring Cloud Gateway with JWT authentication
- **Inter-Service**: OpenFeign clients with circuit breakers (Resilience4j)
- **Internal APIs**: `/internal/**` routes blocked at gateway, used for service-to-service communication

### Authentication Flow
```
Client → API Gateway (JWT validation) → Service (with user context)
```
- JWT tokens managed by `auth-service`
- User context propagated via headers: `X-User-Id`, `X-User-Roles`
- Internal APIs bypass gateway for service-to-service calls

### Data Architecture
- **Database per Service**: PostgreSQL for each service
- **Shared Cache**: Redis for user caching across services
- **Cache Strategy**: Read-through with eviction on write operations

### Key Services

#### User Service (`user-service:8081`)
- **Purpose**: User and role management
- **Database**: PostgreSQL (`user-db:5433`)
- **Cache**: Redis with 10-minute TTL
- **APIs**: Public `/users/**`, `/roles/**` + Internal `/internal/**`

#### Auth Service (`auth-service:8082`)
- **Purpose**: JWT authentication and token validation
- **Dependencies**: Calls User Service via Feign client
- **Resilience**: Circuit breaker with fallback for User Service calls

#### API Gateway (`api-gateway:8080`)
- **Purpose**: Single entry point, authentication, routing
- **Security**: JWT filter on all routes except `/auth/**`
- **Routing**: Dynamic via Eureka + static route definitions

## Testing Strategy

### Test Structure
- **Unit Tests**: Service layer with mocking (`*ServiceImplTest`)
- **Integration Tests**: Controller + repository (`*IntegrationTest`)
- **Cache Tests**: Redis caching functionality (`*CacheTest`)
- **Total**: 64 test methods across all modules

### Test Execution Patterns
```bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dtest=*ServiceImplTest

# Run cache-specific tests
mvn -pl user-service test -Dtest=UserServiceCacheTest

# Integration tests (requires test containers)
mvn -pl user-service test -Dtest=*IntegrationTest
```

## Important Configuration Files

### Core Configurations
- **Root POM**: `/pom.xml` - Dependency management for all modules
- **Database Setup**: `/docker-compose.db.yml` - PostgreSQL + Redis
- **Service Configs**: `*/src/main/resources/application.yml`

### Authentication & Security
- **JWT Filter**: `api-gateway/src/main/java/com/example/apigateway/filter/JwtAuthFilter.java`
- **JWT Utilities**: `common-lib/src/main/java/com/cursor/common/jwt/JwtService.java`
- **Feign Client**: `auth-service/src/main/java/com/example/authservice/client/UserServiceClient.java`

### Cache Configuration
- **Redis Setup**: `user-service/src/main/java/com/example/userservice/config/RedisConfig.java`
- **Cache Documentation**: `/REDIS_CACHE_SETUP.md`

## Deployment

### Kubernetes
- **Manifests**: `/k8s/` directory
- **Namespace**: `microservices`
- **Monitoring**: Prometheus + Grafana configured
- **Databases**: Persistent volumes for PostgreSQL and Redis

### Docker
- Each service has individual Dockerfile
- Multi-stage builds for optimized images
- Health checks via Spring Actuator endpoints

## Development Notes

### Module Dependencies
- **common-lib**: Shared DTOs, JWT utilities, exception handling
- **Service Dependencies**: auth-service → user-service (via Feign)
- **Build Order**: common-lib must be built first

### Port Allocation
- **8761**: Discovery Server (Eureka)
- **8080**: API Gateway (main entry point)
- **8081**: User Service
- **8082**: Auth Service
- **5433**: PostgreSQL (mapped from 5432)
- **6379**: Redis

### Common Development Tasks

**Adding a new endpoint:**
1. Create controller method with appropriate mapping
2. Add service layer implementation
3. Update Swagger documentation
4. Add integration tests
5. Update gateway routing if needed

**Service-to-service communication:**
1. Add method to existing Feign client or create new one
2. Add fallback implementation for circuit breaker
3. Configure retry and timeout policies
4. Use internal API endpoints (bypass gateway)

**Database changes:**
1. Update JPA entities
2. Add database migrations if needed
3. Update service layer methods
4. Clear relevant cache entries
5. Add/update tests