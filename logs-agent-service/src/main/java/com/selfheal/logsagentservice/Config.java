package com.selfheal.logsagentservice;

import com.selfheal.logsagentservice.MCpTools.LogsTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(LogsTools logsTools) {
        return MethodToolCallbackProvider.builder().toolObjects(logsTools).build();
    }
}
