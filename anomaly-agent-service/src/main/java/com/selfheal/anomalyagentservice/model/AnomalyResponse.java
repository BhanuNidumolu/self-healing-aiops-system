package com.selfheal.anomalyagentservice.model;

public class AnomalyResponse {

    private boolean anomaly;
    private String reason;

    public boolean isAnomaly() {
        return anomaly;
    }

    public void setAnomaly(boolean anomaly) {   // <--- REQUIRED METHOD
        this.anomaly = anomaly;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {      // <--- REQUIRED METHOD
        this.reason = reason;
    }
}
