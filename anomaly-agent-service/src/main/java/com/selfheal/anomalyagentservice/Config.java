package com.selfheal.anomalyagentservice;

import com.selfheal.anomalyagentservice.Tools.AnomalyTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(AnomalyTools anomalyTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(anomalyTools)
                .build();
    }
}
