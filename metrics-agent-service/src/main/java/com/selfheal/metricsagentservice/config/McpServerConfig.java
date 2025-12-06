package com.selfheal.metricsagentservice.config;

import com.selfheal.metricsagentservice.service.MetricsTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {
    @Bean
    ToolCallbackProvider toolCallbackProvider(MetricsTools tools) {
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }
}
