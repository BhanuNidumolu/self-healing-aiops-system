package com.selfheal.monitoredservice.controller;

import com.selfheal.monitoredservice.model.ChaosState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LogsController {

    private final ChaosState state;

    public LogsController(ChaosState state) {
        this.state = state;
    }

    @GetMapping("/logs")
    public List<String> getLogs() {
        List<String> logs = new ArrayList<>();
        boolean hasChaos = state.getMemoryLoad() > 0 
                        || !state.getMemoryLeakHolder().isEmpty()
                        || state.getCpuStress().get() 
                        || state.getSlowFactor() > 0;

        logs.add("INFO /health check served in " + (8 + (int)(Math.random() * 15)) + "ms");

        if (hasChaos) {
            if (state.getMemoryLoad() > 20 || !state.getMemoryLeakHolder().isEmpty()) {
                int heap = Math.min(98, 65 + state.getMemoryLoad());
                logs.add("WARN GC Overhead: " + (75 + state.getMemoryLoad()/4) + "% time in GC");
                logs.add("ERROR OutOfMemoryError: Java heap space");
                logs.add("WARN Heap usage: " + heap + "%");
            }
            if (state.getCpuStress().get()) {
                logs.add("WARN CPU usage: " + (90 + (int)(Math.random() * 9)) + "%");
                logs.add("WARN ThreadPool queue: 150 pending");
            }
            if (state.getSlowFactor() > 0) {
                logs.add("WARN Latency: /api/orders took " + (300 + state.getSlowFactor()) + "ms");
                logs.add("ERROR GatewayTimeout: exceeded 5s");
            }
        } else {
            logs.add("INFO Cache hit ratio: 94%");
            logs.add("INFO Thread pool stable");
        }

        logs.add("INFO Disk usage: " + (50 + (int)(Math.random() * 20)) + "%");
        return logs;
    }
}