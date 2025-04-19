package com.rajharit.rajharitsprings.mappers;

import com.rajharit.rajharitsprings.dtos.PriceDto;
import com.rajharit.rajharitsprings.entities.PriceHistory;
import org.springframework.stereotype.Component;

@Component
public class PriceMapper {
    public PriceHistory toEntity(PriceDto dto) {
        return new PriceHistory(dto.getPrice(), dto.getDate());
    }

    public PriceDto toDto(PriceHistory entity) {
        PriceDto dto = new PriceDto();
        dto.setPrice(entity.getPrice());
        dto.setDate(entity.getDate());
        return dto;
    }
}