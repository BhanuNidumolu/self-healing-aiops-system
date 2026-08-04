package com.selfheal.monitoredservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

@RestController
public class ProcessController {

    private final Random random = new Random();

    @GetMapping("/api/process")
    public Map<String, Object> process() {
        // Simulate some work
        try {
            Thread.sleep(random.nextInt(50) + 20);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Map.of(
            "status", "success",
            "message", "Request processed successfully",
            "timestamp", System.currentTimeMillis()
        );
    }
}