package com.selfheal.supervisorservice.controller;

import com.selfheal.supervisorservice.model.SystemReport;
import com.selfheal.supervisorservice.service.SupervisorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class SupervisorController {

    private final SupervisorService service;

    public SupervisorController(SupervisorService service){
        this.service = service;
    }

    @GetMapping("/supervise")
    public Map<String, Object> supervise(){
        return service.supervise();
    }
}
