package com.rajharit.rajharitspringpointvente.mappers;

import com.rajharit.rajharitspringpointvente.dtos.DishDto;
import com.rajharit.rajharitspringpointvente.dtos.DishIngredientDto;
import com.rajharit.rajharitspringpointvente.entities.Dish;
import com.rajharit.rajharitspringpointvente.entities.Ingredient;
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
        dto.setQuantity(ingredient.getRequiredQuantity());
        dto.setUnit(ingredient.getUnit().name());
        return dto;
    }
}
