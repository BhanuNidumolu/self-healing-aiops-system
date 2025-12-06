package com.selfheal.monitoredservice.model;

import lombok.Data;

@Data
public class Metrics {

    private int cpu;
    private int memory;
    private int disk;
    private int latency;
    private int errors;

    private int requestRate;
    private int activeThreads;
    private int gcPauseMs;

    private String timestamp;

    // getters and setters
}
