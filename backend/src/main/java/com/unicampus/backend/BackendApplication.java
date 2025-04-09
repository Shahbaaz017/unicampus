package com.unicampus.backend; // Your package

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Add this import:
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// Add the exclude annotation here:
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}