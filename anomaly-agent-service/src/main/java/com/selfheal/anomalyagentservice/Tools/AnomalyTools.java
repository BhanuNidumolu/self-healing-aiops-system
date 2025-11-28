package com.selfheal.anomalyagentservice.Tools;

import com.selfheal.anomalyagentservice.model.AnomalyResponse;
import com.selfheal.anomalyagentservice.service.AnomalyDetectorService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AnomalyTools {

    private final AnomalyDetectorService service;

    public AnomalyTools(AnomalyDetectorService service) {
        this.service = service;
    }

    @Tool(
            name = "detect_anomaly",
            description = "Detects anomalies using metrics + logs."
    )
    public AnomalyResponse detect(Map<String, Object> input) {

        Map<String, Object> metrics =
                (Map<String, Object>) input.get("metrics");

        Map<String, Object> logs =
                (Map<String, Object>) input.get("logs");

        return service.detect(metrics, logs);
    }
}
