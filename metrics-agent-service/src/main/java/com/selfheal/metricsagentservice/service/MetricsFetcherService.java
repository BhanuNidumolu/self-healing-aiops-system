package com.selfheal.metricsagentservice.service;

import com.selfheal.metricsagentservice.model.MetricsAnalysis;
import com.selfheal.metricsagentservice.model.MetricsRaw;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MetricsFetcherService {

    private final RestTemplate rest = new RestTemplate();

    // === MAIN RAW FETCH FUNCTION (USED BY SUPERVISOR) ===
    public MetricsRaw fetchRawMetrics() {
//        String url = "http://localhost:8081/metrics";
// direct from monitored-service
        String url = "http://monitored-service:8080/metrics";
        MetricsRaw raw = rest.getForObject(url, MetricsRaw.class);
        log.info("📥 RAW METRICS FETCHED → {}", raw);

        return raw;
    }

    // === OLD METHOD (KEPT FOR ANALYSIS UI) ===
    public MetricsAnalysis fetchMetrics() {
        return fetchMetrics("default");
    }

    /**
     * Scenario-based metrics selection: default/memory/cpu/network/db
     * Used for testing + chaos simulation
     */
    public MetricsAnalysis fetchMetrics(String scenario) {
        MetricsRaw raw;

        if (scenario == null || scenario.isBlank() || scenario.equalsIgnoreCase("default")) {
            raw = fetchRawMetrics();  // use NEW RAW FETCH
        } else {
            raw = syntheticMetricsForScenario(scenario.toLowerCase().trim());
            log.warn("⚠ Using synthetic metrics scenario → {}", scenario);
        }

        return toAnalysis(raw, scenario);
    }

    // === Synthetic Values for Chaos Simulation ===
    private MetricsRaw syntheticMetricsForScenario(String scenario) {
        MetricsRaw raw = new MetricsRaw();

        switch (scenario) {
            case "memory":
                raw.setCpu(72);
                raw.setMemory(96);
                raw.setLatency(850);
                raw.setErrors(3);
                break;

            case "cpu":
                raw.setCpu(95);
                raw.setMemory(70);
                raw.setLatency(700);
                raw.setErrors(2);
                break;

            case "db":
                raw.setCpu(65);
                raw.setMemory(68);
                raw.setLatency(900);
                raw.setErrors(4);
                break;

            case "network":
                raw.setCpu(45);
                raw.setMemory(55);
                raw.setLatency(1200);
                raw.setErrors(1);
                break;

            default:
                raw.setCpu(55);
                raw.setMemory(60);
                raw.setLatency(180);
                raw.setErrors(0);
        }
        return raw;
    }

    // === Convert raw → summarized form (used only for UI reporting) ===
    private MetricsAnalysis toAnalysis(MetricsRaw raw, String scenario) {
        MetricsAnalysis analysis = new MetricsAnalysis();

        analysis.setCpu(raw.getCpu());
        analysis.setMemory(raw.getMemory());
        analysis.setLatency(raw.getLatency());
        analysis.setErrors(raw.getErrors());

        analysis.setSummary("CPU: " + raw.getCpu() +
                "%, Memory: " + raw.getMemory() +
                "%, Latency: " + raw.getLatency() +
                "ms, Errors: " + raw.getErrors());

        String status = "OK";
        if (raw.getCpu() > 85 || raw.getMemory() > 85 || raw.getErrors() > 3 || raw.getLatency() > 700)
            status = "WARNING";

        if (raw.getMemory() > 95 || raw.getLatency() > 1000 || "memory".equalsIgnoreCase(scenario))
            status = "CRITICAL";

        analysis.setStatus(status);
        return analysis;
    }
}
