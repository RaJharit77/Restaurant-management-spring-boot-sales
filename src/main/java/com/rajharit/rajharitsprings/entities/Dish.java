package com.rajharit.rajharitsprings.entities;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Dish {
    private int id;
    private String name;
    private double unitPrice;
    private List<Ingredient> ingredients;

    public Dish() {
    }

    public Dish(int id, String name, double unitPrice, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.ingredients = ingredients;
    }

    public double getIngredientCost() {
        return getIngredientCostAtDate(LocalDateTime.now());
    }

    public double getIngredientCostAtDate(LocalDateTime date) {
        return ingredients.stream()
                .mapToDouble(di -> di.getAvailableQuantity() * di.getPriceAtDate(date))
                .sum();
    }

    public double getGrossMargin() {
        return getGrossMarginAtDate(LocalDateTime.now());
    }

    public double getGrossMarginAtDate(LocalDateTime date) {
        return unitPrice - getIngredientCostAtDate(date);
    }

    public double getAvailableQuantity(LocalDateTime date) {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0;
        }

        return ingredients.stream()
                .filter(ingredient -> ingredient.getAvailableQuantity() > 0)
                .mapToDouble(ingredient -> {
                    double ingredientAvailableQuantity = ingredient.getAvailableQuantity(date);
                    double requiredQuantity = ingredient.getAvailableQuantity();
                    return ingredientAvailableQuantity / requiredQuantity;
                })
                .min()
                .orElse(0);
    }
}