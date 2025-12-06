package com.selfheal.monitoredservice.controller;

import com.selfheal.monitoredservice.model.Metrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Random;

@RestController
public class MetricsController {

    private final Random random = new Random();

    @GetMapping("/metrics")
    public Metrics getMetrics() {

        // Base CPU + Memory
        int cpu = 40 + random.nextInt(50);         // 40–90%
        int memory = 50 + random.nextInt(40);      // 50–90%
        int disk = 60 + random.nextInt(35);        // 60–95%

        // Latency increases if CPU or memory high
        int baseLatency = 80 + random.nextInt(100);
        int latency = baseLatency + (cpu / 2) + (memory / 3);

        // Error count increases with poor system health
        int errors = 0;
        if (cpu > 80) errors += random.nextInt(3);
        if (memory > 85) errors += random.nextInt(2);
        if (latency > 250) errors += random.nextInt(4);

        Metrics metrics = new Metrics();
        metrics.setCpu(cpu);
        metrics.setMemory(memory);
        metrics.setDisk(disk);
        metrics.setLatency(latency);
        metrics.setErrors(errors);
        metrics.setTimestamp(Instant.now().toString());

        // Bonus: additional useful metrics
        metrics.setRequestRate(100 + random.nextInt(200));   // req/min
        metrics.setActiveThreads(50 + random.nextInt(50));
        metrics.setGcPauseMs(random.nextInt(30));            // 0-30ms

        return metrics;
    }
}
