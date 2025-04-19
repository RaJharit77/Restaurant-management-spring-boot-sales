package com.rajharit.rajharitsprings.services;

import com.rajharit.rajharitsprings.dtos.DishDto;
import com.rajharit.rajharitsprings.dtos.DishIngredientDto;
import com.rajharit.rajharitsprings.entities.Dish;
import com.rajharit.rajharitsprings.entities.Ingredient;
import com.rajharit.rajharitsprings.entities.Unit;
import com.rajharit.rajharitsprings.exceptions.ResourceNotFoundException;
import com.rajharit.rajharitsprings.mappers.DishMapper;
import com.rajharit.rajharitsprings.dao.DishDAOImpl;
import com.rajharit.rajharitsprings.dao.IngredientDAOImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        List<Ingredient> dishIngredients = new ArrayList<>();

        for (DishIngredientDto dto : ingredients) {
            Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId());
            if (ingredient == null) {
                throw new ResourceNotFoundException("Ingredient not found with id: " + dto.getIngredientId());
            }

            Ingredient dishIngredient = new Ingredient();
            dishIngredient.setId(ingredient.getId());
            dishIngredient.setName(ingredient.getName());
            dishIngredient.setActualPrice(ingredient.getActualPrice());
            dishIngredient.setUnit(dto.getUnit());
            dishIngredient.setAvailableQuantity(dto.getQuantity());

            dishIngredients.add(dishIngredient);
        }

        dish.setIngredients(dishIngredients);
        Dish updatedDish = dishRepository.saveAll(List.of(dish)).get(0);
        return dishMapper.toDto(updatedDish);
    }

    public DishDto createDish(DishDto dishDto) {
        Dish dish = new Dish();
        dish.setName(dishDto.getName());
        dish.setUnitPrice(dishDto.getUnitPrice());

        Dish savedDish = dishRepository.saveAll(List.of(dish)).getFirst();
        return dishMapper.toDto(savedDish);
    }

    public void deleteDishesId(int id) {
        dishRepository.deleteDish(id);
    }
}