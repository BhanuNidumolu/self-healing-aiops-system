package com.selfheal.supervisorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class SupervisorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupervisorServiceApplication.class, args);
    }

}
