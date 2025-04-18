package com.rajharit.rajharitspringpointvente.dao;

import com.rajharit.rajharitspringpointvente.entities.Ingredient;
import com.rajharit.rajharitspringpointvente.entities.PriceHistory;
import com.rajharit.rajharitspringpointvente.entities.StockMovement;
import com.rajharit.rajharitspringpointvente.entities.Unit;

import java.time.LocalDateTime;
import java.util.List;

public interface IngredientDAO {
    List<Ingredient> getAll();

    Ingredient findById(int id);

    List<Ingredient> saveAll(List<Ingredient> ingredients);

    List<StockMovement> getStockMovementsByIngredientId(int ingredientId);

    void deleteIngredient(int id);

    List<Ingredient> filterIngredients(String name, Unit unit, Double minPrice, Double maxPrice, int page, int pageSize);

    void addStockMovement(StockMovement movement);

    Ingredient findByIdAndPriceAndDateAndStockDate(int ingredientId, LocalDateTime priceDate, LocalDateTime stockDate);

    void savePrices(int ingredientId, List<PriceHistory> prices);

    void saveStockMovements(int ingredientId, List<StockMovement> movements);

    void updateCurrentPrice(int ingredientId, double price, LocalDateTime date);
}