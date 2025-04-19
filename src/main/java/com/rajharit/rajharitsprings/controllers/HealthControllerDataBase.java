package com.rajharit.rajharitsprings.controllers;

import com.rajharit.rajharitsprings.config.DataBaseSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/health")
public class HealthControllerDataBase  {
    private final DataBaseSource dataBaseSource;

    public HealthControllerDataBase(DataBaseSource dataBaseSource) {
        this.dataBaseSource = dataBaseSource;
    }

    @GetMapping
    public ResponseEntity<String> checkHealth() {
        try (Connection conn = dataBaseSource.getConnection()) {
            return ResponseEntity.ok("Database connection OK");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Database connection failed: " + e.getMessage());
        }
    }
}