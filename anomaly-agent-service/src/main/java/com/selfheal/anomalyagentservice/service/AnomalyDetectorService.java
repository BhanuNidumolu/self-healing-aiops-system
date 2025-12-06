package com.selfheal.anomalyagentservice.service;

import com.selfheal.anomalyagentservice.model.AnomalyResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AnomalyDetectorService {

    public AnomalyResponse detect(Map<String, Object> metrics, Map<String, Object> logs) {

        AnomalyResponse response = new AnomalyResponse();
        StringBuilder reason = new StringBuilder();

        int cpu = (int) metrics.get("cpu");
        int memory = (int) metrics.get("memory");
        int errors = (int) metrics.get("errors");

        List<String> logErrors = (List<String>) logs.get("errors");

        boolean anomaly = false;

        if (cpu > 85) {
            anomaly = true;
            reason.append("High CPU. ");
        }
        if (memory > 85) {
            anomaly = true;
            reason.append("High memory usage. ");
        }
        if (errors > 5) {
            anomaly = true;
            reason.append("Too many errors in metrics. ");
        }
        if (!logErrors.isEmpty()) {
            anomaly = true;
            reason.append("Critical errors found in logs. ");
        }

        response.setAnomaly(anomaly);
        response.setReason(reason.toString());

        return response;
    }
}
