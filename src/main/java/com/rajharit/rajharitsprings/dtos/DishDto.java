package com.rajharit.rajharitsprings.dtos;

import lombok.Data;
import java.util.List;

@Data
public class DishDto {
    private int id;
    private String name;
    private double unitPrice;
    private List<DishIngredientDto> ingredients;
}