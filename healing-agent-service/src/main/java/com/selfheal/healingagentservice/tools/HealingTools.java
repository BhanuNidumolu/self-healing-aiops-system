package com.selfheal.healingagentservice.tools;

import com.selfheal.healingagentservice.model.HealingResponse;
import com.selfheal.healingagentservice.service.HealingService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HealingTools {

    private final HealingService healingService;

    public HealingTools(HealingService healingService) {
        this.healingService = healingService;
    }

    @Tool(
            name = "apply_healing",
            description = "Executes healing such as restart or scale operations."
    )
    public HealingResponse applyHealing(Map<String, Object> input) {

        String command = (String) input.get("command");  // restart/scale
        String service = (String) input.get("service");

        if (command.equalsIgnoreCase("restart")) {
            return healingService.restartService(service);
        }

        if (command.equalsIgnoreCase("scale")) {
            int count = (int) input.get("count");
            return healingService.scaleService(service, count);
        }

        // fallback
        HealingResponse res = new HealingResponse();
        res.setAction("none");
        res.setStatus("FAILED");
        res.setMessage("Unknown healing command: " + command);

        return res;
    }
}
