package com.rds.app.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class GreetController
{
    @Value("${APP_MESSAGE: Maza aa gya bhai!}")
    private String message;

    @GetMapping
    public Map<String, String> greet()
    {
        return Map.of(
            "message", "Congratulations! You have successfully deployed your first Spring Boot application with Jenkins on AWS.",
            "status", "success",
            "timestamp", LocalDateTime.now().toString(),
            "version", "1.0.0",
            "author", "Ramanuj Das"
        );

    }

    @GetMapping("/health")
    public Map<String, String> healthCheck()
    {
        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString(),
            "version", "1.0.0",
            "author", "Ramanuj Das"
        );
    }

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "application", "Spring Boot on AWS",
                "version", "1.0.0",
                "author", "Ramanuj Das",
                "description", "A simple Spring Boot application deployed on AWS to demonstrate cloud deployment capabilities."
        );
    }

    @GetMapping("/message")
    public String getMessage(){
        return message;
    }


}
