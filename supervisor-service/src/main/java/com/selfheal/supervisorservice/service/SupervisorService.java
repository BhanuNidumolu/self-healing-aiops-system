package com.selfheal.supervisorservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.selfheal.supervisorservice.model.SystemReport;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SupervisorService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SupervisorService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public SystemReport runSupervisor() {

        // 1. Ask the supervisor ChatClient to diagnose & heal using MCP tools.
       return chatClient
                .prompt()
                .user("""
                        Diagnose the current system health using all available MCP tools.
                        If needed, apply healing through the heal agent.
                        """)
                .call()
                .entity(SystemReport.class);
//
//        SystemReport report = new SystemReport();
//
//        try {
//            // 2. Parse the JSON returned by the LLM into a Map.
//            Map<String, Object> root = objectMapper.readValue(
//                    rawJson, new TypeReference<Map<String, Object>>() {}
//            );
//
//            // 3. Safely extract fields expected in SystemReport.
//            Object metrics = root.get("metrics");
//            Object logs = root.get("logs");
//            Object anomaly = root.get("anomaly");
//            Object healingAction = root.get("healingAction");
//            Object finalStatus = root.get("finalStatus");
//
//            if (metrics instanceof Map<?, ?> m) {
//                //noinspection unchecked
//                report.setMetrics((Map<String, Object>) m);
//            }
//            if (logs instanceof Map<?, ?> m) {
//                //noinspection unchecked
//                report.setLogs((Map<String, Object>) m);
//            }
//            if (anomaly instanceof Map<?, ?> m) {
//                //noinspection unchecked
//                report.setAnomaly((Map<String, Object>) m);
//            }
//            if (healingAction instanceof Map<?, ?> m) {
//                //noinspection unchecked
//                report.setHealingAction((Map<String, Object>) m);
//            }
//            if (finalStatus instanceof String s) {
//                report.setFinalStatus(s);
//            } else {
//                report.setFinalStatus("Supervisor response parsed, but no 'finalStatus' field found.");
//            }
//
//        } catch (Exception e) {
//            // 4. If parsing fails, at least return the raw AI response for debugging.
//            report.setFinalStatus("Failed to parse AI JSON. Raw response: " + rawJson);
//        }
//
//        return report;
    }
}
