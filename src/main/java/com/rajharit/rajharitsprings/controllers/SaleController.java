package com.rajharit.rajharitsprings.controllers;

import com.rajharit.rajharitsprings.dtos.DishSoldDto;
import com.rajharit.rajharitsprings.security.ApiKeyManager;
import com.rajharit.rajharitsprings.services.SaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sales")
public class SaleController {
    private final SaleService saleService;
    private final ApiKeyManager apiKeyManager;

    public SaleController(SaleService saleService, ApiKeyManager apiKeyManager) {
        this.saleService = saleService;
        this.apiKeyManager = apiKeyManager;
    }

    @GetMapping
    public ResponseEntity<List<DishSoldDto>> getDishesSold(
            @RequestHeader("X-API-KEY") String apiKey) {
        if (!apiKeyManager.isValidApiKey(apiKey)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(saleService.findAllDeliveredDishes());
    }
}