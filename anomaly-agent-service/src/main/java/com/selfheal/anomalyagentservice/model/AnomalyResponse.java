package com.selfheal.anomalyagentservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnomalyResponse {

    private boolean anomaly;       // true/false if issue detected
    private String reason;         // explanation
    private double confidence;     // probability or score (0.0 - 1.0)

}
