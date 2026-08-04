package com.selfheal.metricsagentservice.model;

import lombok.Data;

@Data
public class MetricsRaw {
    private int cpu;
    private int memory;
    private int latency;
    private int errors;

    private int disk;
    private int requestRate;
    private int activeThreads;
    private int gcPauseMs;

    private String timestamp;
}
