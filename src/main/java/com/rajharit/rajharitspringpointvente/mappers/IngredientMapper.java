package com.rajharit.rajharitspringpointvente.mappers;

import com.rajharit.rajharitspringpointvente.dtos.IngredientDto;
import com.rajharit.rajharitspringpointvente.entities.Ingredient;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class IngredientMapper {
    private final PriceMapper priceMapper;
    private final StockMovementMapper stockMovementMapper;

    public IngredientMapper(PriceMapper priceMapper, StockMovementMapper stockMovementMapper) {
        this.priceMapper = priceMapper;
        this.stockMovementMapper = stockMovementMapper;
    }

    public IngredientDto toDto(Ingredient ingredient) {
        IngredientDto dto = new IngredientDto();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setUnitPrice(ingredient.getUnitPrice());
        dto.setUpdateDateTime(ingredient.getUpdateDateTime());

        dto.setPriceHistory(ingredient.getPriceHistory().stream()
                .map(priceMapper::toDto)
                .collect(Collectors.toList()));

        dto.setStockMovements(ingredient.getStockMovements().stream()
                .map(stockMovementMapper::toDto)
                .collect(Collectors.toList()));

        return dto;
    }

    public Ingredient toEntity(IngredientDto dto) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(dto.getId());
        ingredient.setName(dto.getName());
        ingredient.setUnitPrice(dto.getUnitPrice());
        ingredient.setUpdateDateTime(dto.getUpdateDateTime());
        return ingredient;
    }
}