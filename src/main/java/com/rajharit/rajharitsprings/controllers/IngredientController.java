package com.rajharit.rajharitsprings.controllers;

import com.rajharit.rajharitsprings.dtos.IngredientDto;
import com.rajharit.rajharitsprings.dtos.PriceDto;
import com.rajharit.rajharitsprings.dtos.StockMovementDto;
import com.rajharit.rajharitsprings.services.IngredientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientDto>> getAllIngredients(
            @RequestParam(required = false) Double maxPrice) {
        List<IngredientDto> ingredients = ingredientService.getAllIngredients(maxPrice);
        return new ResponseEntity<>(ingredients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getIngredientById(@PathVariable int id) {
        IngredientDto ingredient = ingredientService.getIngredientById(id);
        return new ResponseEntity<>(ingredient, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<IngredientDto>> createIngredients(@RequestBody List<IngredientDto> ingredientDtos) {
        List<IngredientDto> createdIngredients = ingredientService.saveIngredients(ingredientDtos);
        return new ResponseEntity<>(createdIngredients, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<List<IngredientDto>> updateIngredients(@RequestBody List<IngredientDto> ingredientDtos) {
        List<IngredientDto> updatedIngredients = ingredientService.saveIngredients(ingredientDtos);
        return new ResponseEntity<>(updatedIngredients, HttpStatus.OK);
    }

    @PutMapping("/{ingredientId}/prices")
    public ResponseEntity<Void> updateIngredientPrices(
            @PathVariable int ingredientId,
            @RequestBody List<PriceDto> priceDtos) {
        ingredientService.updatePrices(ingredientId, priceDtos);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{ingredientId}/stockMovements")
    public ResponseEntity<Void> updateIngredientStockMovements(
            @PathVariable int ingredientId,
            @RequestBody List<StockMovementDto> stockMovementDtos) {
        ingredientService.updateStockMovements(ingredientId, stockMovementDtos);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/prices")
    public ResponseEntity<List<PriceDto>> getIngredientPrices(@PathVariable int id) {
        List<PriceDto> prices = ingredientService.getIngredientPrices(id);
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/{id}/stockMovements")
    public ResponseEntity<List<StockMovementDto>> getIngredientStockMovements(@PathVariable int id) {
        List<StockMovementDto> movements = ingredientService.getIngredientStockMovements(id);
        return ResponseEntity.ok(movements);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteIngredientId(int id) {
        ingredientService.deleteIngredientId(id);
        return ResponseEntity.noContent().build();
    }
}