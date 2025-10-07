# Redis Cache Setup for User Service

This document explains how to set up and use Redis cache with the user-service.

## 🚀 Quick Start

### 1. Start Redis Container

```bash
# Start only the databases (PostgreSQL + Redis)
docker compose -f docker-compose.db.yml up -d

# Check if Redis is running
docker ps | grep redis
```

### 2. Start User Service

```bash
cd user-service
mvn spring-boot:run
```

### 3. Test Cache Functionality

```bash
# Test user creation
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'

# Test user retrieval (first call - from database)
curl http://localhost:8081/users/1

# Test user retrieval (second call - from cache)
curl http://localhost:8081/users/1
```

## 📋 Cache Configuration

### Cache Annotations Used

| Method      | Annotation                                        | Purpose                             |
| ----------- | ------------------------------------------------- | ----------------------------------- |
| `getById()` | `@Cacheable(value = "users", key = "#id")`        | Cache user by ID                    |
| `create()`  | `@CacheEvict(value = "users", allEntries = true)` | Clear all cache on create           |
| `update()`  | `@CacheEvict(value = "users", key = "#id")`       | Clear specific user cache on update |
| `delete()`  | `@CacheEvict(value = "users", key = "#id")`       | Clear specific user cache on delete |

### Cache Configuration Details

```yaml
# application.yml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000 # 10 minutes
      cache-null-values: false
      enable-statistics: true
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
```

## 🔧 Redis Configuration

### Redis Server Settings

```yaml
# docker-compose.db.yml
redis:
  image: redis:7-alpine
  container_name: user-redis
  restart: unless-stopped
  ports:
    - "6379:6379"
  command: redis-server --appendonly yes --maxmemory 256mb --maxmemory-policy allkeys-lru
```

### Key Features

- **Persistence**: AOF (Append Only File) enabled
- **Memory Management**: 256MB max memory with LRU eviction
- **Connection Pooling**: Lettuce connection pool configured
- **JSON Serialization**: Automatic serialization/deserialization

## 🧪 Testing Cache

### Manual Testing

1. **Create a user** - This will clear the cache
2. **Get user by ID** - First call hits database, second call hits cache
3. **Update user** - This will clear the specific user cache
4. **Get user by ID again** - Will hit database again (cache was cleared)

### Performance Testing

```bash
# Test cache performance
time curl http://localhost:8081/users/1  # First call (database)
time curl http://localhost:8081/users/1  # Second call (cache - should be faster)
```

### Redis CLI Testing

```bash
# Connect to Redis container
docker exec -it user-redis redis-cli

# List all keys
KEYS *

# Get cache statistics
INFO stats

# Monitor Redis commands
MONITOR
```

## 📊 Cache Statistics

The cache configuration includes statistics. You can monitor cache performance through:

1. **Actuator Endpoint**: `http://localhost:8081/actuator/caches`
2. **Redis CLI**: `INFO stats`
3. **Application Logs**: Cache hit/miss information

## 🛠️ Troubleshooting

### Common Issues

1. **Redis Connection Failed**

   ```bash
   # Check if Redis is running
   docker ps | grep redis

   # Check Redis logs
   docker logs user-redis
   ```

2. **Cache Not Working**

   - Verify `@EnableCaching` is present in configuration
   - Check Redis connection in application logs
   - Ensure cache annotations are properly applied

3. **Serialization Issues**
   - Verify Jackson configuration in `RedisConfig`
   - Check if entities implement `Serializable` (not required with Jackson)

### Debug Commands

```bash
# Check Redis connection
docker exec -it user-redis redis-cli ping

# View Redis memory usage
docker exec -it user-redis redis-cli info memory

# Clear all cache
docker exec -it user-redis redis-cli FLUSHALL
```

## 🔄 Cache Eviction Strategy

- **Create**: Evicts all entries (allEntries = true)
- **Update**: Evicts specific user entry (key = "#id")
- **Delete**: Evicts specific user entry (key = "#id")
- **TTL**: Automatic expiration after 10 minutes

## 📈 Performance Benefits

- **Reduced Database Load**: Frequently accessed users served from cache
- **Faster Response Times**: Cache hits are significantly faster than database queries
- **Scalability**: Multiple service instances can share the same Redis cache
- **Memory Efficiency**: LRU eviction policy prevents memory overflow

## 🚀 Production Considerations

1. **Redis Cluster**: For high availability
2. **Cache Warming**: Pre-populate cache with frequently accessed data
3. **Monitoring**: Set up Redis monitoring and alerting
4. **Backup**: Configure Redis persistence and backup strategies
5. **Security**: Enable Redis authentication in production
