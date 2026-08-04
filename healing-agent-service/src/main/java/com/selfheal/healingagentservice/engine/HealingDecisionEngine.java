package com.selfheal.healingagentservice.engine;

import java.util.Map;

public class HealingDecisionEngine {

    public static Map<String,Object> decide(Map<String,Object> metrics, Map<String,Object> logs) {

        int memory = (int) metrics.getOrDefault("memory", 0);
        int cpu = (int) metrics.getOrDefault("cpu", 0);
        int latency = (int) metrics.getOrDefault("latency", 0);
        int errors = (int) metrics.getOrDefault("errors", 0);

        String logString = logs.toString().toLowerCase();

        // ---- RULES ----
        if(memory > 90 || logString.contains("outofmemory") || logString.contains("heap")) {
            return Map.of("command","restart","service","payment-service","reason","Memory Leak");
        }
        if(logString.contains("deadlock") || logString.contains("connectionpool")) {
            return Map.of("command","restart","service","payment-service","reason","DB Lock Issue");
        }
        if(cpu > 95) {
            return Map.of("command","scale","service","payment-service","count",3,"reason","CPU Spike");
        }
        if(latency > 1500) {
            return Map.of("command","scale","service","payment-service","count",2,"reason","Latency High");
        }

        return Map.of("command","none","reason","System stable");
    }
}