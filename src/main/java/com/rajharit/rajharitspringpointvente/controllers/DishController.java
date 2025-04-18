package com.rajharit.rajharitspringpointvente.controllers;

import com.rajharit.rajharitspringpointvente.dtos.DishDto;
import com.rajharit.rajharitspringpointvente.dtos.DishIngredientDto;
import com.rajharit.rajharitspringpointvente.services.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<DishDto>> getAllDishes() {
        List<DishDto> dishes = dishService.getAllDishes();
        return ResponseEntity.ok(dishes);
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<DishDto> updateDishIngredients(
            @PathVariable int id,
            @RequestBody List<DishIngredientDto> ingredients) {
        DishDto updatedDish = dishService.updateDishIngredients(id, ingredients);
        return ResponseEntity.ok(updatedDish);
    }
}