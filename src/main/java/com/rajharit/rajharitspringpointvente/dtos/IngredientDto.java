package com.rajharit.rajharitspringpointvente.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class IngredientDto {
    private int id;
    private String name;
    private double unitPrice;
    private LocalDateTime updateDateTime;
    private List<PriceDto> priceHistory;
    private List<StockMovementDto> stockMovements;
}
