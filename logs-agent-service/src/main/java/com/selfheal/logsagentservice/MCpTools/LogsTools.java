package com.selfheal.logsagentservice.MCpTools;

import com.selfheal.logsagentservice.model.LogAnalysis;
import com.selfheal.logsagentservice.service.LogsFetcherService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class LogsTools {

    private final LogsFetcherService service;

    public LogsTools(LogsFetcherService service) {
        this.service = service;
    }

    @Tool(
            name = "fetch_logs",
            description = "Fetch and analyze latest logs from the monitored service."
    )
    public LogAnalysis fetchLogs() {
        System.out.println("LOGS FETCHED (default remote logs)");
        return service.fetchLogs();
    }

    @Tool(
            name = "fetch_logs_scenario",
            description = """
                    Fetch synthetic logs for a specific failure scenario.
                    Supported values: 'db', 'memory', 'cpu', 'network', 'circuit', 'security'.
                    """)
    public LogAnalysis fetchLogsScenario(String scenario) {
        System.out.println("LOGS FETCHED (scenario: " + scenario + ")");
        return service.fetchLogs(scenario);
    }
}
