package com.selfheal.metricsagentservice.service;

import com.selfheal.metricsagentservice.model.MetricsAnalysis;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MetricsTools {

    private final MetricsFetcherService service;

    public MetricsTools(MetricsFetcherService service) {
        this.service = service;
    }

    @Tool(name = "fetch_metrics", description = "Return analyzed system metrics from the monitored service.")
    public Map<String, Object> fetchMetrics() {
        System.out.println("METRICS FETCHED (default)");
        MetricsAnalysis analysis = service.fetchMetrics();
        return toMap(analysis);
    }

    @Tool(
            name = "fetch_metrics_scenario",
            description = """
                    Return analyzed system metrics for a given test scenario.
                    Supported values: 'memory', 'cpu', 'db', 'network', 'default'.
                    """)
    public Map<String, Object> fetchMetricsScenario(String scenario) {
        System.out.println("METRICS FETCHED (scenario: " + scenario + ")");
        MetricsAnalysis analysis = service.fetchMetrics(scenario);
        return toMap(analysis);
    }

    private Map<String, Object> toMap(MetricsAnalysis a) {
        if (a == null) {
            return Map.of("status", "UNAVAILABLE", "summary", "Metrics could not be fetched");
        }

        return Map.of(
                "cpu", a.getCpu(),
                "memory", a.getMemory(),
                "latency", a.getLatency(),
                "errors", a.getErrors(),
                "status", a.getStatus(),
                "summary", a.getSummary()
        );
    }
}
