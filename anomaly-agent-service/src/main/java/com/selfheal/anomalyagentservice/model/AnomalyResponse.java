package com.selfheal.anomalyagentservice.model;

import lombok.Data;

@Data
public class AnomalyResponse {
    private boolean anomaly;
    private String reason;
}
