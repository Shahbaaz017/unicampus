// backend/src/main/java/com/unicampus/backend/HealthController.java
package com.unicampus.backend; // Use your actual package name

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController // Marks this class as a REST controller
public class HealthController {

    @GetMapping("/api/health") // Maps HTTP GET requests for /api/health to this method
    public Map<String, String> checkHealth() {
        // Create a simple response map (automatically converted to JSON)
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Backend service is running!");
        return response;
    }
}