package com.selfheal.metricsagentservice.controller;

import com.selfheal.metricsagentservice.model.MetricsAnalysis;
import com.selfheal.metricsagentservice.service.MetricsFetcherService;
import com.selfheal.metricsagentservice.service.MetricsTools;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MetricsAgentController {

    private final MetricsFetcherService service;
    private final MetricsTools  tools;

    public MetricsAgentController(MetricsFetcherService service, MetricsTools tools) {
        this.service = service;
        this.tools = tools;
    }

    @GetMapping("/analyze")
    public MetricsAnalysis analyzeMetrics() {
        return service.fetchMetrics();
    }

    @GetMapping("/new-")
    public Map<String, Object> newMetrics() {
        return tools.fetchMetrics();
    }
}
