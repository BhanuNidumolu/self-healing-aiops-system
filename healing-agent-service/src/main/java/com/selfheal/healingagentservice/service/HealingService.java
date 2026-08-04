package com.selfheal.healingagentservice.service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class HealingService {

    public Map<String,Object> restartService(String service) {
        return Map.of(
                "action", "restart",
                "service", service,
                "status", "SUCCESS",
                "message", "Service " + service + " restarted successfully."
        );
    }

    public Map<String,Object> scaleService(String service, int count) {
        return Map.of(
                "action", "scale",
                "service", service,
                "instances", count,
                "status", "SUCCESS",
                "message", "Scaled to " + count + " instances successfully."
        );
    }
}
