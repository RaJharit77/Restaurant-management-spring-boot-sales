package com.rajharit.rajharitspringpointvente.dtos;

import lombok.Data;

@Data
public class DishIngredientDto {
    private int ingredientId;
    private double quantity;
    private String unit;
}
