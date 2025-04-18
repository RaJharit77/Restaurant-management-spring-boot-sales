package com.rajharit.rajharitspringpointvente.services;

import com.rajharit.rajharitspringpointvente.dtos.DishDto;
import com.rajharit.rajharitspringpointvente.dtos.DishIngredientDto;
import com.rajharit.rajharitspringpointvente.entities.Dish;
import com.rajharit.rajharitspringpointvente.entities.Ingredient;
import com.rajharit.rajharitspringpointvente.entities.Unit;
import com.rajharit.rajharitspringpointvente.exceptions.ResourceNotFoundException;
import com.rajharit.rajharitspringpointvente.mappers.DishMapper;
import com.rajharit.rajharitspringpointvente.dao.DishDAOImpl;
import com.rajharit.rajharitspringpointvente.dao.IngredientDAOImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishService {
    private final DishDAOImpl dishRepository;
    private final IngredientDAOImpl ingredientRepository;
    private final DishMapper dishMapper;

    public DishService(DishDAOImpl dishRepository,
                       IngredientDAOImpl ingredientRepository,
                       DishMapper dishMapper) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
        this.dishMapper = dishMapper;
    }

    public List<DishDto> getAllDishes() {
        List<Dish> dishes = dishRepository.getAll();
        return dishes.stream()
                .map(dishMapper::toDto)
                .collect(Collectors.toList());
    }

    public DishDto updateDishIngredients(int id, List<DishIngredientDto> ingredients) {
        Dish dish = dishRepository.findById(id);
        if (dish == null) {
            throw new ResourceNotFoundException("Dish not found with id: " + id);
        }

        List<Ingredient> dishIngredients = ingredients.stream()
                .map(dto -> {
                    Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId());
                    if (ingredient == null) {
                        throw new ResourceNotFoundException("Ingredient not found with id: " + dto.getIngredientId());
                    }
                    ingredient.setRequiredQuantity(dto.getQuantity());
                    ingredient.setUnit(Unit.valueOf(dto.getUnit()));
                    return ingredient;
                })
                .collect(Collectors.toList());

        dish.setIngredients(dishIngredients);
        Dish updatedDish = (Dish) dishRepository.saveAll((List<Dish>) dish);
        return dishMapper.toDto(updatedDish);
    }
}