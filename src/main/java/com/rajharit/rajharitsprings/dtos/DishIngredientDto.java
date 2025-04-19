package com.rajharit.rajharitsprings.dtos;

import com.rajharit.rajharitsprings.entities.Unit;
import lombok.Data;

@Data
public class DishIngredientDto {
    private int ingredientId;
    private String name;
    private double quantity;
    private Unit unit;
}