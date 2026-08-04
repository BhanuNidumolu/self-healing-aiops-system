package com.selfheal.healingagentservice.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HealingTools {

    private final StringRedisTemplate redis;
    private static final String DEFAULT_SERVICE = "monitored-service";

    public HealingTools(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Tool(name = "apply_healing", 
          description = "Execute healing: restart, scale, circuit_break, or request_human_approval")
    public Map<String, Object> applyHealing(Map<String, Object> input) {
        String command = (String) input.getOrDefault("command", "restart");
        String service = (String) input.getOrDefault("service", DEFAULT_SERVICE);

        return switch (command.toLowerCase()) {
            case "restart" -> restartContainer(service);
            case "scale" -> {
                Integer count = input.get("count") instanceof Number ? 
                    ((Number) input.get("count")).intValue() : null;
                yield scaleService(service, count);
            }
            case "circuit_break" -> circuitBreak(service, input);
            case "request_human_approval" -> requestHumanApproval(service, input);
            default -> Map.of("status", "FAILED", "reason", "Unknown command: " + command);
        };
    }

    private Map<String, Object> restartContainer(String service) {
        try {
            log.warn("🔁 Restarting {}", service);
            new ProcessBuilder("docker", "restart", service).start().waitFor();
            return Map.of("status", "SUCCESS", "action", "restart", "service", service);
        } catch (Exception e) {
            return Map.of("status", "FAILED", "error", e.getMessage());
        }
    }

    private Map<String, Object> scaleService(String service, Integer count) {
        if (count == null) return Map.of("status", "FAILED", "reason", "Missing count");
        return Map.of("status", "SUCCESS", "action", "scale", "service", service, "instances", count);
    }

    private Map<String, Object> circuitBreak(String service, Map<String, Object> input) {
        int duration = input.get("duration") instanceof Number ? 
            ((Number) input.get("duration")).intValue() : 60;
        
        log.warn("🔴 Circuit BREAK for {} ({}s)", service, duration);
        
        redis.opsForValue().set("circuit:" + service + ":state", "OPEN", duration, TimeUnit.SECONDS);
        redis.opsForValue().set("circuit:" + service + ":reason", 
            (String) input.getOrDefault("reason", "Agent triggered"), duration, TimeUnit.SECONDS);

        boolean captureHeap = Boolean.TRUE.equals(input.get("capture_heap_dump"));
        if (captureHeap) {
            redis.opsForValue().set("heap_dump:" + service, 
                "captured_at_" + System.currentTimeMillis(), 3600, TimeUnit.SECONDS);
        }

        return Map.of("status", "SUCCESS", "action", "circuit_break", 
            "service", service, "duration_sec", duration, "circuit_state", "OPEN");
    }

    private Map<String, Object> requestHumanApproval(String service, Map<String, Object> input) {
        String requestId = "approval_req:" + System.currentTimeMillis();
        redis.opsForHash().putAll(requestId, Map.of(
            "service", service,
            "requested_action", input.getOrDefault("requested_action", "restart"),
            "reason", input.getOrDefault("reason", "Safety escalation"),
            "status", "PENDING"
        ));
        redis.expire(requestId, 3600, TimeUnit.SECONDS);

        return Map.of("status", "PENDING", "action", "request_human_approval", 
            "service", service, "request_id", requestId);
    }
}