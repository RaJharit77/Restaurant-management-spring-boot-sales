package com.rajharit.rajharitsprings.dtos;

import com.rajharit.rajharitsprings.entities.Unit;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IngredientPriceDto {
    private String ingredientName;
    private double currentPrice;
    private Unit unit;
    private LocalDateTime updateDateTime;
}
