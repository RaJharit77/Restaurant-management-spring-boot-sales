package com.rajharit.rajharitsprings.mappers;

import com.rajharit.rajharitsprings.dtos.DishDto;
import com.rajharit.rajharitsprings.dtos.DishIngredientDto;
import com.rajharit.rajharitsprings.entities.Dish;
import com.rajharit.rajharitsprings.entities.Ingredient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DishMapper {
    public DishDto toDto(Dish dish) {
        DishDto dto = new DishDto();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setUnitPrice(dish.getUnitPrice());

        if (dish.getIngredients() != null) {
            List<DishIngredientDto> ingredientDtos = dish.getIngredients().stream()
                    .map(this::toDishIngredientDto)
                    .collect(Collectors.toList());
            dto.setIngredients(ingredientDtos);
        }

        return dto;
    }

    private DishIngredientDto toDishIngredientDto(Ingredient ingredient) {
        DishIngredientDto dto = new DishIngredientDto();
        dto.setIngredientId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setQuantity(ingredient.getAvailableQuantity());
        dto.setUnit(ingredient.getUnit());
        return dto;
    }
}