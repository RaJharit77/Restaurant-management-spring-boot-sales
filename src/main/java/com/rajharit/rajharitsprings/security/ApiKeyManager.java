package com.rajharit.rajharitsprings.security;

import org.springframework.stereotype.Component;

@Component
public class ApiKeyManager {
    private static final String VALID_API_KEY = "RESTAURANT-API-KEY";

    public boolean isValidApiKey(String apiKey) {
        return VALID_API_KEY.equals(apiKey);
    }
}