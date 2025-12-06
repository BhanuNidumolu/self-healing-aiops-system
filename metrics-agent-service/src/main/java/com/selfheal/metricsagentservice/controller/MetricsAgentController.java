package com.selfheal.metricsagentservice.controller;

import com.selfheal.metricsagentservice.model.MetricsAnalysis;
import com.selfheal.metricsagentservice.service.MetricsFetcherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsAgentController {

    private final MetricsFetcherService service;

    public MetricsAgentController(MetricsFetcherService service) {
        this.service = service;
    }

    @GetMapping("/analyze")
    public MetricsAnalysis analyzeMetrics() {
        return service.fetchMetrics();
    }
}
