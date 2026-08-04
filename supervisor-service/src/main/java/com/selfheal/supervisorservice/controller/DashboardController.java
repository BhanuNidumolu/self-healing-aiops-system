package com.selfheal.supervisorservice.controller;

import com.selfheal.supervisorservice.service.EventStoreService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final StringRedisTemplate redis;
    private final EventStoreService eventStore;

    public DashboardController(StringRedisTemplate redis, EventStoreService eventStore) {
        this.redis = redis;
        this.eventStore = eventStore;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new LinkedHashMap<>();

        String circuitState = redis.opsForValue().get("circuit:monitored-service:state");
        status.put("circuit_state", circuitState != null ? circuitState : "CLOSED");
        status.put("timestamp", Instant.now().toString());

        Long queueDepth = redis.opsForList().size("circuit:monitored-service:queue");
        status.put("queued_requests", queueDepth != null ? queueDepth : 0);

        List<String> active = new ArrayList<>();
        if (circuitState != null) active.add("circuit_breaker");
        if ("true".equals(redis.opsForValue().get("dedup:monitored-service:enabled"))) active.add("deduplication");
        if (redis.opsForValue().get("throttle:monitored-service:max_rps") != null) active.add("rate_limiting");
        status.put("active_protections", active);

        return status;
    }

    @GetMapping("/events")
    public List<Map<String, Object>> getEvents(
            @RequestParam(defaultValue = "10") int limit) {
        // PRIMARY: Query DynamoDB for persistent history
        List<Map<String, Object>> dbEvents = eventStore.getRecentEvents(limit);

        if (!dbEvents.isEmpty()) {
            return dbEvents;
        }

        // FALLBACK: Redis (for events before DynamoDB is set up)
        List<Map<String, Object>> events = new ArrayList<>();
        String circuitReason = redis.opsForValue().get("circuit:monitored-service:reason");
        if (circuitReason != null) {
            events.add(Map.of(
                "type", "CIRCUIT_BREAK",
                "reason", circuitReason,
                "timestamp", Instant.now().toString(),
                "service", "monitored-service"
            ));
        }
        return events;
    }

    @GetMapping("/safety-status")
    public Map<String, Object> getSafetyStatus() {
        Map<String, Object> status = new LinkedHashMap<>();

        String hourKey = "heal_count:monitored-service:" + java.time.LocalDateTime.now().getHour();
        String count = redis.opsForValue().get(hourKey);
        status.put("heals_this_hour", count != null ? Integer.parseInt(count) : 0);
        status.put("heal_quota", 3);

        Set<String> approvalKeys = redis.keys("approval_req:*");
        status.put("pending_approvals", approvalKeys != null ? approvalKeys.size() : 0);

        return status;
    }

    @PostMapping("/reset")
    public Map<String, Object> resetSystem() {
        redis.delete("circuit:monitored-service:state");
        redis.delete("circuit:monitored-service:reason");
        redis.delete("circuit:monitored-service:queue");
        redis.delete("heap_dump:monitored-service");
        redis.delete("dedup:monitored-service:enabled");
        redis.delete("throttle:monitored-service:max_rps");

        // Clear heal count for current hour
        String hourKey = "heal_count:monitored-service:" + java.time.LocalDateTime.now().getHour();
        redis.delete(hourKey);

        return Map.of(
            "status", "SUCCESS",
            "message", "All protections cleared. Circuit is CLOSED.",
            "timestamp", Instant.now().toString()
        );
    }
}