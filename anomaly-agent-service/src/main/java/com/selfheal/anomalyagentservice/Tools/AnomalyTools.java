package com.selfheal.anomalyagentservice.Tools;

import com.selfheal.anomalyagentservice.model.AnomalyResponse;
import com.selfheal.anomalyagentservice.service.AnomalyDetectorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class AnomalyTools {

    private final AnomalyDetectorService anomalyDetectorService;

    @Tool(
            name = "detect_anomaly",
            description = "Returns anomaly:true/false and reason based on metrics"
    )
    public AnomalyResponse detectAnomaly(double cpu, double memory, double latency, double errorRate) {


        log.info("Anomoly Toool called-------->anamoly calllleddedd with matrics {}", List.of(cpu, memory, latency, errorRate));
        return anomalyDetectorService.detectAnomaly(cpu,memory,latency,errorRate);


    }
}
