package com.ecomapp.productapp.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckApi {

    @GetMapping("/health")
    public String healthCheck() {
        return "Product Service is up and running!";
    }

}
