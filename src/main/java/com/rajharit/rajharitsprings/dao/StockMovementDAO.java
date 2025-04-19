package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.StockMovement;

import java.util.List;

public interface StockMovementDAO {
    void saveStockMovement(StockMovement stockMovement);
    List<StockMovement> getStockMovementsByIngredientId(int ingredientId);
}
