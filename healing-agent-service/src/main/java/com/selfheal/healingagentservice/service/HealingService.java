package com.selfheal.healingagentservice.service;

import com.selfheal.healingagentservice.model.HealingResponse;
import org.springframework.stereotype.Service;

@Service
public class HealingService {

    public HealingResponse restartService(String service) {
        HealingResponse res = new HealingResponse();
        res.setAction("restart");
        res.setStatus("SUCCESS");
        res.setMessage("Service " + service + " restarted successfully.");
        return res;
    }

    public HealingResponse scaleService(String service, int count) {
        HealingResponse res = new HealingResponse();
        res.setAction("scale");
        res.setStatus("SUCCESS");
        res.setMessage("Service " + service + " scaled to " + count + " instances.");
        return res;
    }
}
