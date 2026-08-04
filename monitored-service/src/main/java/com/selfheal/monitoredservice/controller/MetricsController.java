package com.selfheal.monitoredservice.controller;

import com.selfheal.monitoredservice.model.ChaosState;
import com.selfheal.monitoredservice.model.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Random;

@RestController
@Slf4j
public class MetricsController {

    private final ChaosState state;

    public MetricsController(ChaosState state) {
        this.state = state;
    }

    @GetMapping("/metrics")
    public Metrics getMetrics() {

        Random random = new Random();

        int cpu = 35 + random.nextInt(25) + (state.getMemoryLoad() / 200);
        if(state.getCpuStress().get()) cpu += 30 + random.nextInt(20);
        cpu = Math.min(cpu, 100);

        long total = Runtime.getRuntime().totalMemory();
        long used  = total - Runtime.getRuntime().freeMemory();
        int memory = (int)((used * 100) / total);

        // Disk simulated but optional — static or random
        int disk = 40 + random.nextInt(20);                 // future: mount monitoring here

        int latency = 120 + memory * 3 + state.getSlowFactor();
        int requestRate = 150 + random.nextInt(100);        // baseline req/sec simulation
        int activeThreads = Thread.activeCount();           // real JVM count
        int gcPauseMs = (memory > 80) ? random.nextInt(200) + 100 : random.nextInt(50);

        int errors = 0;
        if(memory > 75) errors += random.nextInt(4);
        if(cpu > 85) errors += random.nextInt(3);
        if(latency > 700) errors += 3;

        Metrics m = new Metrics();
        m.setCpu(cpu);
        m.setMemory(memory);
        m.setDisk(disk);
        m.setLatency(latency);
        m.setErrors(errors);
        m.setRequestRate(requestRate);
        m.setActiveThreads(activeThreads);
        m.setGcPauseMs(gcPauseMs);
        m.setTimestamp(Instant.now().toString());

        log.warn("📊 METRICS --> {}", m);
        return m;
    }

}
