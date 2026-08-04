package com.selfheal.metricsagentservice.service;

import com.selfheal.metricsagentservice.model.MetricsAnalysis;
import com.selfheal.metricsagentservice.model.MetricsRaw;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component
public class MetricsTools {

    private final MetricsFetcherService service;

    public MetricsTools(MetricsFetcherService service) {
        this.service = service;
    }

    @Tool(name="fetch_metrics", description="Return RAW system metrics for Supervisor AI")
    public Map<String, Object> fetchMetrics() {
        MetricsRaw raw = service.fetchRawMetrics();     // new method - defined below
        return toRawMap(raw);
    }

    @Tool(name="fetch_metrics_analysis", description="Return analyzed metrics summary")
    public Map<String, Object> fetchMetricsAnalysis() {
        MetricsAnalysis analysis = service.fetchMetrics();
        return toAnalysisMap(analysis);
    }

    private Map<String, Object> toRawMap(MetricsRaw r) {
        return Map.of(
                "cpu", r.getCpu(),
                "memory", r.getMemory(),
                "disk", r.getDisk(),
                "latency", r.getLatency(),
                "errors", r.getErrors(),
                "requestRate", r.getRequestRate(),
                "activeThreads", r.getActiveThreads(),
                "gcPauseMs", r.getGcPauseMs(),
                "timestamp", r.getTimestamp()
        );
    }

    private Map<String, Object> toAnalysisMap(MetricsAnalysis a) {
        return Map.of(
                "cpu", a.getCpu(),
                "memory", a.getMemory(),
                "latency", a.getLatency(),
                "errors", a.getErrors(),
                "status", a.getStatus(),
                "summary", a.getSummary(),
                "timestamp", System.currentTimeMillis()
        );
    }
}
