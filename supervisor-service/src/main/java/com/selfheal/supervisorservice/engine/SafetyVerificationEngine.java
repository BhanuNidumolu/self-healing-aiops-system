package com.selfheal.supervisorservice.engine;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class SafetyVerificationEngine {

    private final StringRedisTemplate redis;

    public SafetyVerificationEngine(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public SafetyResult verify(Map<String, Object> proposedAction, 
                                Map<String, Object> metrics,
                                Map<String, Object> logs) {
        
        String command = (String) proposedAction.getOrDefault("command", "none");
        String service = (String) proposedAction.getOrDefault("service", "monitored-service");
        
        // CONSTRAINT 1: Business Hours - No restart 9AM-6PM
        LocalTime now = LocalTime.now();
        boolean isBusinessHours = now.isAfter(LocalTime.of(9, 0)) 
                                 && now.isBefore(LocalTime.of(18, 0));
        
        if ("restart".equals(command) && isBusinessHours) {
            return SafetyResult.override(
                "BUSINESS_HOURS",
                "Restart blocked during 9AM-6PM. Using circuit_break instead.",
                Map.of("command", "circuit_break", "service", service, 
                       "duration", 60, "reason", "Business hours safety override")
            );
        }

        // CONSTRAINT 2: Auto-Heal Quota - Max 3/hour
        String hourKey = "heal_count:" + service + ":" + java.time.LocalDateTime.now().getHour();
        String count = redis.opsForValue().get(hourKey);
        int healCount = count == null ? 0 : Integer.parseInt(count);
        
        if (healCount >= 3) {
            return SafetyResult.escalate(
                "QUOTA_EXHAUSTED",
                "Auto-heal quota exceeded (3/hour). Human approval required."
            );
        }

        // CONSTRAINT 3: High Latency + Low Errors → Circuit Break, NOT Restart
        int latency = toInt(metrics.get("latency"));
        int errors = toInt(metrics.get("errors"));
        if ("restart".equals(command) && latency > 1500 && errors < 3) {
            return SafetyResult.override(
                "PREFER_CIRCUIT_BREAK",
                "High latency with low errors. Overriding to circuit_break.",
                Map.of("command", "circuit_break", "service", service, 
                       "duration", 60, "reason", "Latency spike - preserving in-flight requests")
            );
        }

        // CONSTRAINT 4: Memory Leak → Circuit Break + Heap Dump
        int memory = toInt(metrics.get("memory"));
        String logStr = logs.toString().toLowerCase();
        boolean isMemoryLeak = memory > 85 || logStr.contains("outofmemory") || logStr.contains("heap");
        if ("restart".equals(command) && isMemoryLeak) {
            return SafetyResult.override(
                "MEMORY_LEAK_PROTOCOL",
                "Memory leak detected. Circuit breaking instead of restart.",
                Map.of("command", "circuit_break", "service", service, 
                       "duration", 120, "capture_heap_dump", true,
                       "reason", "Memory leak - capturing heap before any action")
            );
        }

        // CONSTRAINT 5: DB Issues → Block Restart, Suggest Pool Tune
        boolean isDbIssue = logStr.contains("deadlock") || logStr.contains("connectionpool");
        if ("restart".equals(command) && isDbIssue) {
            return SafetyResult.override(
                "DB_ISSUE_PROTOCOL",
                "DB connectivity issue. Restart won't help. Using circuit_break + alert.",
                Map.of("command", "circuit_break", "service", service,
                       "duration", 60, "reason", "DB issue - restart ineffective")
            );
        }

        // All passed
        redis.opsForValue().increment(hourKey);
        redis.expire(hourKey, 3600, TimeUnit.SECONDS);
        return SafetyResult.allow("All safety constraints passed.");
    }

    private static int toInt(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    public static class SafetyResult {
        public enum Decision { ALLOW, BLOCK, ESCALATE, OVERRIDE }
        
        private final Decision decision;
        private final String ruleId;
        private final String explanation;
        private final Map<String, Object> overriddenAction;

        private SafetyResult(Decision d, String r, String e, Map<String, Object> o) {
            this.decision = d; this.ruleId = r; this.explanation = e; this.overriddenAction = o;
        }

        public static SafetyResult allow(String e) { 
            return new SafetyResult(Decision.ALLOW, null, e, null); 
        }
        public static SafetyResult block(String r, String e) { 
            return new SafetyResult(Decision.BLOCK, r, e, null); 
        }
        public static SafetyResult escalate(String r, String e) { 
            return new SafetyResult(Decision.ESCALATE, r, e, null); 
        }
        public static SafetyResult override(String r, String e, Map<String, Object> o) { 
            return new SafetyResult(Decision.OVERRIDE, r, e, o); 
        }

        public Decision getDecision() { return decision; }
        public String getRuleId() { return ruleId; }
        public String getExplanation() { return explanation; }
        public Map<String, Object> getOverriddenAction() { return overriddenAction; }
        public boolean isAllowed() { return decision == Decision.ALLOW || decision == Decision.OVERRIDE; }
    }
}