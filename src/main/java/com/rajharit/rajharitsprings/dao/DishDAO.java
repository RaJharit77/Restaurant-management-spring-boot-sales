package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.Dish;
import com.rajharit.rajharitsprings.entities.Ingredient;

import java.util.List;

public interface DishDAO {
    List<Dish> getAll();

    Dish findById(int id);

    List<Dish> saveAll(List<Dish> dishes);

    List<Dish> filterDish(String name, double unitPrice, List<Ingredient> dishIngredient);

    void deleteDish(int id);
}