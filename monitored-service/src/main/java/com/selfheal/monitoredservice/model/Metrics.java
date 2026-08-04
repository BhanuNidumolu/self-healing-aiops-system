package com.selfheal.monitoredservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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



}
