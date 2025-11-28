package com.selfheal.supervisorservice.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    /**
     * ChatClient that knows about ALL MCP tools from:
     * - metric-agent
     * - logs-agent
     * - anomaly-agent
     * - heal-agent
     *
     * Spring AI auto-creates the SyncMcpToolCallbackProvider based on
     * spring.ai.mcp.client.* properties.
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel,
                                 SyncMcpToolCallbackProvider toolCallbackProvider) {

        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        You are a self-healing supervisor for a distributed system.
                        You have access to multiple specialized MCP agents:
                        - metrics agent: use its tools to inspect CPU, memory, latency, error rate.
                        - logs agent: use its tools to read/search logs and find errors/warnings.
                        - anomaly agent: use its tools to detect anomalies and root causes.
                        - heal agent: use its tools to apply safe healing actions (restart/scale/etc).

                        ALWAYS:
                        1. Call metrics and logs tools first to collect the current state.
                        2. Use anomaly tools to interpret whether this is a real issue.
                        3. If needed, call heal tools to fix the issue.
                        4. Return a JSON object with this structure ONLY:
                           {
                             "metrics": { ... },
                             "logs": { ... },
                             "anomaly": { ... },
                             "healingAction": { ... },
                             "finalStatus": "short human-readable summary"
                           }
                        Do not wrap the JSON in markdown. No extra text.
                        """)
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();
    }
}
