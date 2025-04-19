package com.rajharit.rajharitsprings.dtos;

import com.rajharit.rajharitsprings.entities.Unit;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class IngredientDto {
    private int id;
    private String name;
    private double unitPrice;
    private Unit unit;
    private LocalDateTime updateDateTime;
    private List<PriceDto> priceHistory;
    private List<StockMovementDto> stockMovements;
    private double availableQuantity;
}
