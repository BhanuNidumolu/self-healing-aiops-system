package com.selfheal.monitoredservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {

        Map<String, Object> health = new HashMap<>();

        // Top-level service status
        health.put("status", "UP");
        health.put("timestamp", Instant.now().toString());

        // System metrics (synthetic but realistic)
        Map<String, Object> system = new HashMap<>();
        system.put("cpu", 63);              // %
        system.put("memory", 71);           // %
        system.put("disk", 78);             // %
        system.put("uptime", "3h 21m 14s");
        system.put("latencyMs", 128);

        // DB health info
        Map<String, Object> db = new HashMap<>();
        db.put("status", "DEGRADED");
        db.put("activeConnections", 47);
        db.put("maxPoolSize", 50);
        db.put("slowQueries", 5);
        db.put("lastError", "Timeout while acquiring connection");

        // Cache (Redis) health
        Map<String, Object> cache = new HashMap<>();
        cache.put("status", "UP");
        cache.put("hitRate", "73%");
        cache.put("missRate", "27%");
        cache.put("latencyMs", 3);

        // Microservice dependencies
        Map<String, Object> dependencies = new HashMap<>();
        dependencies.put("inventory-service", "UP");
        dependencies.put("payment-service", "DOWN");
        dependencies.put("notification-service", "UP");
        dependencies.put("analytics-service", "UP");

        // Thread and request info
        Map<String, Object> runtime = new HashMap<>();
        runtime.put("activeThreads", 112);
        runtime.put("queuedRequests", 7);
        runtime.put("requestRate", "142 req/min");

        // Bundle everything
        health.put("system", system);
        health.put("database", db);
        health.put("cache", cache);
        health.put("dependencies", dependencies);
        health.put("runtime", runtime);

        return health;
    }
}
