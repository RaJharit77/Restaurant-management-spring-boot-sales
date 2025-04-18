package com.rajharit.rajharitspringpointvente.dao;

import com.rajharit.rajharitspringpointvente.entities.StockMovement;

import java.util.List;

public interface StockMovementDAO {
    void saveStockMovement(StockMovement stockMovement);
    List<StockMovement> getStockMovementsByIngredientId(int ingredientId);
}
