package com.selfheal.monitoredservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LogsController {

    @GetMapping("/logs")
    public List<String> getLogs() {
        return List.of(
                // Critical DB issues
                "ERROR TimeoutException: DB connection failed after 30 seconds",
                "ERROR SQLException: Deadlock detected on orders_table",
                "ERROR ConnectionPoolExhausted: All 50 DB connections in use",
                "WARN Slow query detected: SELECT * FROM payments took 912ms",
                "WARN DB connection retry attempt 4/5",

                // JVM and memory issues
                "WARN GC Overhead: 92% time spent in GC cycle",
                "ERROR OutOfMemoryError: Java heap space",
                "INFO GC completed: freed 256MB",
                "WARN Memory usage high: 82%",

                // API latency issues
                "WARN High latency: /api/orders took 540ms",
                "ERROR GatewayTimeoutException: /api/payments exceeded 5s threshold",
                "INFO Request served: /api/products in 65ms",

                // Authentication / security warnings
                "WARN Authentication failed for user: admin",
                "ERROR Invalid JWT token for request /api/secure/payments",
                "INFO Login attempt for user customer_23",

                // System warnings / info
                "WARN Disk usage at 79%",
                "INFO Scheduled backup completed in 13s",
                "INFO Worker thread pool resized from 50 → 75",

                // Microservice communication issues
                "WARN Service Mesh: Retry attempt 2 for inventory-service",
                "ERROR CircuitBreaker OPEN for shipping-service",
                "INFO CircuitBreaker HALF-OPEN: testing downstream service",

                // Cache layer warnings
                "WARN Cache miss rate high: 47%",
                "INFO Cache refreshed: products-cache",
                "ERROR RedisTimeout: Redis did not respond in 200ms",

                // Network issues
                "WARN Network latency spike detected: 310ms",
                "ERROR Failed to reach payment-gateway after 3 retries",

                // Successful logs
                "INFO Request served successfully for /api/health",
                "INFO Request served successfully for /api/orders/1234"
        );
    }
}
