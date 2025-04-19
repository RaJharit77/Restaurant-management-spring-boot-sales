package com.rajharit.rajharitsprings.mappers;

import com.rajharit.rajharitsprings.dtos.IngredientDto;
import com.rajharit.rajharitsprings.entities.Ingredient;
import com.rajharit.rajharitsprings.entities.PriceHistory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
        dto.setUnitPrice(ingredient.getActualPrice());
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
        ingredient.setActualPrice(dto.getUnitPrice());
        ingredient.setUnit(dto.getUnit());
        ingredient.setUpdateDateTime(dto.getUpdateDateTime());

        if (dto.getPriceHistory() == null || dto.getPriceHistory().isEmpty()) {
            PriceHistory defaultPrice = new PriceHistory(
                    dto.getUnitPrice(),
                    dto.getUpdateDateTime()
            );
            ingredient.setPriceHistory(List.of(defaultPrice));
        } else {
            ingredient.setPriceHistory(dto.getPriceHistory().stream()
                    .map(priceMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        return ingredient;
    }
}