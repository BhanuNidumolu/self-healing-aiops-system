package com.selfheal.logsagentservice.service;

import com.selfheal.logsagentservice.model.LogAnalysis;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LogsFetcherService {

    private final RestTemplate rest = new RestTemplate();

    public LogAnalysis fetchLogs() {
        // Default: call monitored-service directly
        String url = "http://localhost:8081/logs";

        List<String> logs = rest.getForObject(url, List.class);
        if (logs == null) return null;

        return analyze(logs);
    }

    /**
     * Scenario-based log simulation.
     * scenario: "db", "memory", "cpu", "network", "circuit", "security", or null → default.
     */
    public LogAnalysis fetchLogs(String scenario) {
        if (scenario == null || scenario.isBlank()) {
            return fetchLogs();
        }

        String s = scenario.toLowerCase().trim();
        List<String> logs;

        switch (s) {
            case "memory":
                logs = Arrays.asList(
                        "WARN GC Overhead: 94% time spent in GC",
                        "ERROR OutOfMemoryError: Java heap space",
                        "WARN Heap usage critical: 98%",
                        "WARN ThreadPool starvation detected: only 2 worker threads active",
                        "ERROR ServiceResponseTimeout: request /api/orders exceeded 12s",
                        "WARN Allocation failure occurred in region Eden",
                        "ERROR JVM fatal error: unable to create new native thread",
                        "INFO GC cycle completed: freed 128MB"
                );
                break;

            case "cpu":
                logs = Arrays.asList(
                        "WARN High CPU usage detected: 96%",
                        "WARN ThreadPool queue size exceeded threshold: 150 pending tasks",
                        "WARN Request latency increased: /api/orders 850ms",
                        "ERROR ServiceDegradation: /api/payments failing SLO latency",
                        "INFO Scaling recommendation: increase replicas from 2 → 4"
                );
                break;

            case "db":
                logs = Arrays.asList(
                        "ERROR TimeoutException: DB connection failed after 30 seconds",
                        "ERROR SQLException: Deadlock detected on orders_table",
                        "ERROR ConnectionPoolExhausted: All 50 DB connections in use",
                        "WARN Slow query detected: SELECT * FROM payments took 912ms",
                        "WARN DB connection retry attempt 4/5"
                );
                break;

            case "network":
                logs = Arrays.asList(
                        "WARN Network latency spike detected: 310ms",
                        "ERROR Failed to reach payment-gateway after 3 retries",
                        "WARN DNS resolution delay for host payments.external.com",
                        "WARN PacketLoss: 7% detected on eth0"
                );
                break;

            case "circuit":
                logs = Arrays.asList(
                        "WARN Service Mesh: Retry attempt 2 for inventory-service",
                        "ERROR CircuitBreaker OPEN for shipping-service",
                        "INFO CircuitBreaker HALF-OPEN: testing downstream service",
                        "WARN Downstream dependency unstable: shipping-service"
                );
                break;

            case "security":
                logs = Arrays.asList(
                        "WARN Authentication failed for user: admin",
                        "ERROR Invalid JWT token for request /api/secure/payments",
                        "WARN Multiple failed login attempts for user: root",
                        "WARN Possible brute-force attack detected from IP 192.168.1.45"
                );
                break;

            default:
                // Unknown scenario → fallback to default remote logs
                return fetchLogs();
        }

        return analyze(logs);
    }

    private LogAnalysis analyze(List<String> logs) {
        LogAnalysis analysis = new LogAnalysis();
        analysis.setRawLogs(logs);

        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (String log : logs) {
            if (log.startsWith("WARN")) warnings.add(log);
            if (log.startsWith("ERROR")) errors.add(log);
        }

        analysis.setWarnings(warnings);
        analysis.setErrors(errors);

        // Improved root cause detection
        String rootCause = "No major issue found";

        String all = String.join(" | ", logs).toLowerCase();

        if (all.contains("outofmemory") || all.contains("heap") || all.contains("gc overhead")) {
            rootCause = "Memory leak / heap exhaustion";
        } else if (all.contains("db") || all.contains("sql") || all.contains("connectionpoolexhausted")) {
            rootCause = "Database connection failure";
        } else if (all.contains("circuitbreaker")) {
            rootCause = "Downstream microservice failure (CircuitBreaker OPEN)";
        } else if (all.contains("network") || all.contains("failed to reach") || all.contains("packetloss")) {
            rootCause = "Network connectivity issue";
        } else if (all.contains("authentication") || all.contains("invalid jwt") || all.contains("brute-force")) {
            rootCause = "Authentication / security issue";
        } else if (all.contains("high cpu") || all.contains("degradation")) {
            rootCause = "CPU saturation / service degradation";
        } else if (all.contains("slow query") || all.contains("timeoutexception")) {
            rootCause = "Slow database queries / timeouts";
        }

        analysis.setRootCause(rootCause);
        return analysis;
    }
}
