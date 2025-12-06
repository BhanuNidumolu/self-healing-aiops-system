package com.selfheal.supervisorservice.controller;

import com.selfheal.supervisorservice.model.SystemReport;
import com.selfheal.supervisorservice.service.SupervisorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupervisorController {

    private final SupervisorService supervisorService;

    public SupervisorController(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    @GetMapping("/supervise")
    public SystemReport supervise() {
        return supervisorService.runSupervisor();
    }
}
