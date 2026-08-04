package com.selfheal.supervisorservice.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventStoreService {

    private final DynamoDbClient dynamoDb;
    private static final String TABLE_NAME = "sentinel-events";

    public EventStoreService(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    @PostConstruct
public void initTable() {
    // Run in a separate thread so it never blocks Spring Boot context initialization
    CompletableFuture.runAsync(() -> {
        try {
            dynamoDb.describeTable(DescribeTableRequest.builder().tableName(TABLE_NAME).build());
            log.info("DynamoDB table '{}' already exists", TABLE_NAME);
        } catch (ResourceNotFoundException e) {
            try {
                CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(TABLE_NAME)
                    .keySchema(
                        KeySchemaElement.builder().attributeName("event_id").keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName("timestamp").keyType(KeyType.RANGE).build()
                    )
                    .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("event_id").attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName("timestamp").attributeType(ScalarAttributeType.S).build()
                    )
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();

                dynamoDb.createTable(request);
                log.info("DynamoDB table '{}' created successfully", TABLE_NAME);
            } catch (Exception ex) {
                log.error("Failed to create DynamoDB table: {}", ex.getMessage());
            }
        } catch (Exception e) {
            log.warn("Could not connect to local DynamoDB on startup: {}", e.getMessage());
        }
    });
}

    public void saveEvent(Map<String, Object> superviseResult) {
        try {
            String eventId = "evt-" + System.currentTimeMillis();
            String timestamp = Instant.now().toString();

            Map<String, AttributeValue> item = new HashMap<>();
            item.put("event_id", AttributeValue.builder().s(eventId).build());
            item.put("timestamp", AttributeValue.builder().s(timestamp).build());

            Map<String, Object> proposal = (Map<String, Object>) superviseResult.getOrDefault("llm_proposal", Map.of());
            Map<String, Object> finalAction = (Map<String, Object>) superviseResult.getOrDefault("final_action", Map.of());
            Map<String, Object> metrics = (Map<String, Object>) superviseResult.getOrDefault("metrics", Map.of());

            item.put("service", AttributeValue.builder().s((String) proposal.getOrDefault("service", "monitored-service")).build());
            item.put("issue_type", AttributeValue.builder().s(inferIssueType(metrics)).build());
            item.put("proposed_action", AttributeValue.builder().s((String) proposal.getOrDefault("command", "none")).build());
            item.put("safety_decision", AttributeValue.builder().s((String) superviseResult.getOrDefault("safety_decision", "UNKNOWN")).build());
            item.put("safety_rule", AttributeValue.builder().s(extractRuleId((String) superviseResult.getOrDefault("safety_explanation", ""))).build());
            item.put("final_action", AttributeValue.builder().s((String) finalAction.getOrDefault("command", "none")).build());
            item.put("metrics_json", AttributeValue.builder().s(metrics.toString()).build());
            item.put("reason", AttributeValue.builder().s((String) superviseResult.getOrDefault("safety_explanation", "")).build());

            long ttl = Instant.now().getEpochSecond() + (30 * 24 * 60 * 60);
            item.put("ttl", AttributeValue.builder().n(String.valueOf(ttl)).build());

            dynamoDb.putItem(PutItemRequest.builder().tableName(TABLE_NAME).item(item).build());
            log.info("Event persisted: {}", eventId);
        } catch (Exception e) {
            log.error("Failed to persist: {}", e.getMessage());
        }
    }

    public List<Map<String, Object>> getRecentEvents(int limit) {
        try {
            ScanRequest request = ScanRequest.builder().tableName(TABLE_NAME).limit(limit).build();
            ScanResponse response = dynamoDb.scan(request);
            return response.items().stream()
                .sorted((a, b) -> {
                    String tsA = a.getOrDefault("timestamp", AttributeValue.builder().s("").build()).s();
                    String tsB = b.getOrDefault("timestamp", AttributeValue.builder().s("").build()).s();
                    return tsB.compareTo(tsA);
                })
                .map(this::toMap)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to query: {}", e.getMessage());
            return List.of();
        }
    }

    private String inferIssueType(Map<String, Object> metrics) {
        int memory = toInt(metrics.get("memory"));
        int latency = toInt(metrics.get("latency"));
        int errors = toInt(metrics.get("errors"));
        if (memory > 85) return "MEMORY_LEAK";
        if (latency > 2000 && errors < 3) return "LATENCY_SPIKE";
        if (errors > 10) return "HIGH_ERROR_RATE";
        if (latency > 2000) return "DEGRADED_PERFORMANCE";
        return "SYSTEM_CHECK";
    }

    private String extractRuleId(String explanation) {
        if (explanation == null) return "NONE";
        if (explanation.contains("BUSINESS_HOURS")) return "BUSINESS_HOURS";
        if (explanation.contains("MEMORY_LEAK_PROTOCOL")) return "MEMORY_LEAK_PROTOCOL";
        if (explanation.contains("QUOTA_EXHAUSTED")) return "QUOTA_EXHAUSTED";
        if (explanation.contains("PREFER_CIRCUIT_BREAK")) return "PREFER_CIRCUIT_BREAK";
        if (explanation.contains("DB_ISSUE_PROTOCOL")) return "DB_ISSUE_PROTOCOL";
        return "NONE";
    }

    private Map<String, Object> toMap(Map<String, AttributeValue> item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("event_id", item.get("event_id") != null ? item.get("event_id").s() : "");
        map.put("timestamp", item.get("timestamp") != null ? item.get("timestamp").s() : "");
        map.put("service", item.get("service") != null ? item.get("service").s() : "");
        map.put("issue_type", item.get("issue_type") != null ? item.get("issue_type").s() : "");
        map.put("proposed_action", item.get("proposed_action") != null ? item.get("proposed_action").s() : "");
        map.put("safety_decision", item.get("safety_decision") != null ? item.get("safety_decision").s() : "");
        map.put("safety_rule", item.get("safety_rule") != null ? item.get("safety_rule").s() : "");
        map.put("final_action", item.get("final_action") != null ? item.get("final_action").s() : "");
        map.put("reason", item.get("reason") != null ? item.get("reason").s() : "");
        return map;
    }

    private static int toInt(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }
}