package com.selfheal.monitoredservice.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CircuitBreakerFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redis;

    public CircuitBreakerFilter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        String serviceName = "monitored-service"; // In multi-instance, read from env
        String circuitState = redis.opsForValue().get("circuit:" + serviceName + ":state");
        
        // Check deduplication
        String dedupEnabled = redis.opsForValue().get("dedup:" + serviceName + ":enabled");
        if ("true".equals(dedupEnabled)) {
            String requestId = request.getHeader("X-Request-ID");
            if (requestId != null) {
                Boolean isNew = redis.opsForValue().setIfAbsent("dedup:" + serviceName + ":" + requestId, "1", 
                    30, java.util.concurrent.TimeUnit.SECONDS);
                if (Boolean.FALSE.equals(isNew)) {
                    response.setStatus(200);
                    response.getWriter().write("{\"status\":\"deduplicated\",\"message\":\"Duplicate request suppressed\"}");
                    return;
                }
            }
        }

        // Check throttling
        String throttleRps = redis.opsForValue().get("throttle:" + serviceName + ":max_rps");
        if (throttleRps != null) {
            // Simplified: check request count in current second window
            String secondKey = "throttle:" + serviceName + ":count:" + (System.currentTimeMillis() / 1000);
            Long currentCount = redis.opsForValue().increment(secondKey);
            redis.expire(secondKey, 2, java.util.concurrent.TimeUnit.SECONDS);
            
            if (currentCount != null && currentCount > Integer.parseInt(throttleRps)) {
                response.setStatus(429);
                response.setHeader("Retry-After", "5");
                response.getWriter().write("{\"error\":\"Too Many Requests\",\"message\":\"Rate limit active\"}");
                return;
            }
        }

        // Circuit breaker logic
        if ("OPEN".equals(circuitState)) {
            // Queue the request
            String queuedRequest = UUID.randomUUID().toString();
            redis.opsForList().rightPush("circuit:" + serviceName + ":queue", queuedRequest);
            
            response.setStatus(503);
            response.setHeader("Retry-After", "10");
            response.setHeader("X-Circuit-State", "OPEN");
            response.getWriter().write("{\"error\":\"Service Unavailable\",\"message\":\"Circuit breaker is OPEN. Request queued.\",\"circuit\":\"OPEN\"}");
            return;
        }

        if ("HALF_OPEN".equals(circuitState)) {
            // Allow 10% of traffic through (simple hash-based)
            boolean allowed = Math.random() < 0.1;
            if (!allowed) {
                response.setStatus(503);
                response.setHeader("X-Circuit-State", "HALF_OPEN");
                response.getWriter().write("{\"error\":\"Service Unavailable\",\"message\":\"Circuit HALF-OPEN. Request rejected during probe phase.\"}");
                return;
            }
            response.setHeader("X-Circuit-State", "HALF_OPEN");
        }

        response.setHeader("X-Circuit-State", circuitState == null ? "CLOSED" : circuitState);
        filterChain.doFilter(request, response);
    }
}