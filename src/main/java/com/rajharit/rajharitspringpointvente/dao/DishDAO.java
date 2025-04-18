package com.rajharit.rajharitspringpointvente.dao;

import com.rajharit.rajharitspringpointvente.entities.Dish;
import com.rajharit.rajharitspringpointvente.entities.Ingredient;

import java.util.List;

public interface DishDAO {
    List<Dish> getAll();

    Dish findById(int id);

    List<Dish> saveAll(List<Dish> dishes);

    void deleteDish(int id);

    List<Dish> filterDish(String name, double unitPrice, List<Ingredient> dishIngredient);
}