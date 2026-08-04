package com.selfheal.supervisorservice.service;

import com.selfheal.supervisorservice.engine.SafetyVerificationEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SupervisorService {

    private final SafetyVerificationEngine safetyEngine;
    private final EventStoreService eventStore;
    private final ChatClient chatClient;
    private final RestTemplate rest = new RestTemplate();

    @Value("${MONITORED_SERVICE_URL:http://monitored-service:8080}")
    private String monitoredServiceUrl;

    @Value("${HEALING_SERVICE_URL:http://healing-agent:8080}")
    private String healingServiceUrl;

    public SupervisorService(SafetyVerificationEngine safetyEngine, EventStoreService eventStore, ChatClient chatClient) {
        this.safetyEngine = safetyEngine;
        this.eventStore = eventStore;
        this.chatClient = chatClient;
    }

    public Map<String, Object> supervise() {
        // Fetch raw metrics and logs directly from monitored-service
        Map<String, Object> metrics = fetchMetrics();
        List<String> rawLogs = fetchLogs();
        Map<String, Object> logs = Map.of("rawLogs", rawLogs != null ? rawLogs : List.of());

        log.info("Metrics: {}", metrics);
        log.info("Logs: {}", logs);

        // --- Fine-Tuned Qwen LLM decision generation via llama-server ---
        String prompt = "System Telemetry:\nMetrics: " + metrics + "\nLogs: " + logs;
        log.info("Routing telemetry to fine-tuned Qwen model...");
        
        String llmResponse = chatClient.prompt().user(prompt).call().content();
        log.info("Qwen Model Output: {}", llmResponse);

        // Map LLM output to proposal
        Map<String, Object> proposal = Map.of(
            "command", "restart", 
            "service", "monitored-service", 
            "reason", llmResponse != null ? llmResponse : "Qwen Anomaly Classification"
        );

        log.info("Proposed: {}", proposal);

        SafetyVerificationEngine.SafetyResult safety = safetyEngine.verify(proposal, metrics, logs);
        log.info("Safety: {} | Rule: {}", safety.getDecision(), safety.getRuleId());

        Map<String, Object> finalAction;
        String safetyExplanation;

        switch (safety.getDecision()) {
            case ALLOW -> {
                finalAction = proposal;
                safetyExplanation = "Approved: " + safety.getExplanation();
            }
            case OVERRIDE -> {
                finalAction = safety.getOverriddenAction();
                safetyExplanation = "OVERRIDDEN [" + safety.getRuleId() + "]: " + safety.getExplanation();
                log.warn("{}", safetyExplanation);
            }
            case BLOCK -> {
                finalAction = Map.of("command", "none", "reason", "Blocked: " + safety.getExplanation());
                safetyExplanation = "BLOCKED [" + safety.getRuleId() + "]: " + safety.getExplanation();
            }
            case ESCALATE -> {
                finalAction = Map.of(
                    "command", "request_human_approval",
                    "service", proposal.getOrDefault("service", "monitored-service"),
                    "requested_action", proposal.get("command"),
                    "reason", safety.getExplanation()
                );
                safetyExplanation = "ESCALATED [" + safety.getRuleId() + "]: " + safety.getExplanation();
            }
            default -> {
                finalAction = Map.of("command", "none");
                safetyExplanation = "Unknown";
            }
        }

        Map<String, Object> healingResult = Map.of("status", "NO_ACTION");
        if (safety.getDecision() == SafetyVerificationEngine.SafetyResult.Decision.ALLOW && !"none".equals(finalAction.get("command"))) {
            try {
                healingResult = rest.postForObject(healingServiceUrl + "/execute", finalAction, Map.class);
                log.info("Healing: {}", healingResult);
            } catch (Exception e) {
                log.error("Failed to trigger healing execution: {}", e.getMessage());
                healingResult = Map.of("status", "ERROR", "message", e.getMessage());
            }
        }

        Map<String, Object> report = Map.of(
            "metrics", metrics != null ? metrics : Map.of(),
            "logs", logs,
            "llm_proposal", proposal,
            "safety_decision", safety.getDecision().toString(),
            "safety_explanation", safetyExplanation,
            "final_action", finalAction,
            "healing_result", healingResult,
            "finalStatus", safetyExplanation
        );

        eventStore.saveEvent(report);
        return report;
    }

    private Map<String, Object> fetchMetrics() {
        try {
            return rest.getForObject(monitoredServiceUrl + "/metrics", Map.class);
        } catch (Exception e) {
            log.error("Error fetching metrics from {}: {}", monitoredServiceUrl, e.getMessage());
            return Map.of();
        }
    }

    private List<String> fetchLogs() {
        try {
            return rest.getForObject(monitoredServiceUrl + "/logs", List.class);
        } catch (Exception e) {
            log.error("Error fetching logs from {}: {}", monitoredServiceUrl, e.getMessage());
            return List.of();
        }
    }
}