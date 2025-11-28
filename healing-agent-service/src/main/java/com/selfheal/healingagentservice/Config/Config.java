package com.selfheal.healingagentservice.Config;

import com.selfheal.healingagentservice.tools.HealingTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    private final HealingTools healingTools;

    public Config(HealingTools healingTools) {
        this.healingTools = healingTools;
    }

    @Bean
    public ToolCallbackProvider toolCallbackProvider(HealingTools healingTools) {
        return MethodToolCallbackProvider.builder().toolObjects(healingTools).build();
    }
}
