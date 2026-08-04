package com.selfheal.supervisorservice.engine;

import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class HealingDecisionEngineV2 {

    // Safely convert any number to int
    private static int toInt(Object value){
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    public static Map<String, Object> decide(Map<String,Object> metrics, Map<String,Object> logs){

        int memory  = toInt(metrics.get("memory"));
        int cpu     = toInt(metrics.get("cpu"));
        int latency = toInt(metrics.get("latency"));
        int errors  = toInt(metrics.get("errors"));

        String logString = logs.toString().toLowerCase();

        // ------- RULE BASED HEALING --------

        if(memory > 90 || logString.contains("heap") || logString.contains("outofmemory")){
            return Map.of("command","restart","service","monitored-service","reason","Memory Leak detected");
        }

        if(logString.contains("deadlock") || logString.contains("connectionpool")){
            return Map.of("command","restart","service","monitored-service","reason","Database Lock Issue");
        }

        if(cpu > 92){
            return Map.of("command","scale","service","monitored-service","count",3,"reason","High CPU load");
        }

        if( latency > 1500){
            return Map.of("command","scale","service","monitored-service","count",2,"reason","High latency");
        }

        return Map.of("command","none","reason","System stable");
    }
}
