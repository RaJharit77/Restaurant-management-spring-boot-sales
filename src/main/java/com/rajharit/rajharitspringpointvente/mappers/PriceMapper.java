package com.rajharit.rajharitspringpointvente.mappers;

import com.rajharit.rajharitspringpointvente.dtos.PriceDto;
import com.rajharit.rajharitspringpointvente.entities.PriceHistory;
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