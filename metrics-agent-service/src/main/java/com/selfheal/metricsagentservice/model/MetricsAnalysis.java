package com.selfheal.metricsagentservice.model;

public class MetricsAnalysis {
    private String status;
    private String summary;
    private int cpu;
    private int memory;
    private int latency;
    private int errors;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public int getCpu() { return cpu; }
    public void setCpu(int cpu) { this.cpu = cpu; }

    public int getMemory() { return memory; }
    public void setMemory(int memory) { this.memory = memory; }

    public int getLatency() { return latency; }
    public void setLatency(int latency) { this.latency = latency; }

    public int getErrors() { return errors; }
    public void setErrors(int errors) { this.errors = errors; }
}
