package com.ecomapp.cartapp.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckApi {
    @GetMapping("/health")
    public String healthCheck() {
        return "Cart Service is up and running!";
    }

    @GetMapping
    public String status() {
        return "Cart Service is operational.";
    }
}
