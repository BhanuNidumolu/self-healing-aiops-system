package com.selfheal.metricsagentservice.service;

import com.selfheal.metricsagentservice.model.MetricsAnalysis;
import com.selfheal.metricsagentservice.model.MetricsRaw;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MetricsFetcherService {

    private final RestTemplate rest = new RestTemplate();

    public MetricsAnalysis fetchMetrics() {
        return fetchMetrics("default");
    }

    /**
     * Scenario-based metrics.
     * scenario: "default", "memory", "cpu", "db", "network", etc.
     */
    public MetricsAnalysis fetchMetrics(String scenario) {
        MetricsRaw raw;

        if (scenario == null || scenario.isBlank() || scenario.equalsIgnoreCase("default")) {
            String url = "http://localhost:8081/metrics";
            raw = rest.getForObject(url, MetricsRaw.class);
            if (raw == null) return null;
        } else {
            raw = syntheticMetricsForScenario(scenario.toLowerCase().trim());
        }

        return toAnalysis(raw, scenario);
    }

    private MetricsRaw syntheticMetricsForScenario(String scenario) {
        MetricsRaw raw = new MetricsRaw();

        switch (scenario) {
            case "memory":
                raw.setCpu(72);
                raw.setMemory(96);      // very high
                raw.setLatency(850);    // slow due to GC
                raw.setErrors(3);
                break;

            case "cpu":
                raw.setCpu(95);         // CPU saturated
                raw.setMemory(70);
                raw.setLatency(700);
                raw.setErrors(2);
                break;

            case "db":
                raw.setCpu(65);
                raw.setMemory(68);
                raw.setLatency(900);    // DB wait time
                raw.setErrors(4);
                break;

            case "network":
                raw.setCpu(45);
                raw.setMemory(55);
                raw.setLatency(1200);   // huge latency
                raw.setErrors(1);
                break;

            default:
                // Fallback: pretend normal-ish load
                raw.setCpu(55);
                raw.setMemory(60);
                raw.setLatency(180);
                raw.setErrors(0);
        }

        return raw;
    }

    private MetricsAnalysis toAnalysis(MetricsRaw raw, String scenario) {
        MetricsAnalysis analysis = new MetricsAnalysis();

        analysis.setCpu(raw.getCpu());
        analysis.setMemory(raw.getMemory());
        analysis.setLatency(raw.getLatency());
        analysis.setErrors(raw.getErrors());

        String summary = "CPU: " + raw.getCpu() +
                "%, Memory: " + raw.getMemory() +
                "%, Latency: " + raw.getLatency() +
                "ms, Errors: " + raw.getErrors();

        analysis.setSummary(summary);

        // Status logic
        String status = "OK";

        if (raw.getCpu() > 85 || raw.getMemory() > 85 || raw.getErrors() > 3 || raw.getLatency() > 700) {
            status = "WARNING";
        }
        if (raw.getMemory() > 95 || raw.getLatency() > 1000 || "memory".equalsIgnoreCase(scenario)) {
            status = "CRITICAL";
        }

        analysis.setStatus(status);
        return analysis;
    }
}
