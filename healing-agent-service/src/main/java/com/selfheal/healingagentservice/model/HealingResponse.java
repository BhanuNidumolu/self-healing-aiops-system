package com.selfheal.healingagentservice.model;

import lombok.Data;

@Data
public class HealingResponse {
    private String action;   // restart/scale
    private String status;   // SUCCESS/FAILED
    private String message;  // human message
}
