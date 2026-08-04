package com.selfheal.anomalyagentservice.service;

import com.selfheal.anomalyagentservice.model.AnomalyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnomalyDetectorService {
    Logger log = LoggerFactory.getLogger(AnomalyDetectorService.class);
    public AnomalyResponse detectAnomaly(double cpu, double memory, double latency, double errorRate) {

        boolean anomaly = false;
        StringBuilder reason = new StringBuilder();

        if(cpu > 75) {
            anomaly = true;
            reason.append("High CPU usage detected. ");
        }

        if(memory > 80) {
            anomaly = true;
            reason.append("Memory nearing exhaustion. ");
        }

        if(latency > 2000) {
            anomaly = true;
            reason.append("Response latency critical. ");
        }

        if(errorRate > 0.05) {
            anomaly = true;
            reason.append("Error rate too high. ");
        }
        log.info("Anomoly Service detected--------->anomaly {}", anomaly);
        return new AnomalyResponse(
                anomaly,
                reason.length() > 0 ? reason.toString() : "No anomaly detected",
                anomaly ? 0.90 : 0.30
        );
    }
}
