package com.selfheal.healingagentservice.controller;

import com.selfheal.healingagentservice.tools.HealingTools;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class HealingController {

    private final HealingTools healingTools;

    public HealingController(HealingTools healingTools) {
        this.healingTools = healingTools;
    }

    @PostMapping("/execute")
    public Map<String, Object> execute(@RequestBody Map<String, Object> action) {
        return healingTools.applyHealing(action);
    }
}