package com.selfheal.supervisorservice.model;

import lombok.Data;
import java.util.Map;

@Data
public class SystemReport {

    private Map<String, Object> metrics;
    private Map<String, Object> logs;
    private Map<String, Object> anomaly;
    private Map<String, Object> healingAction;
    private String finalStatus;
}
