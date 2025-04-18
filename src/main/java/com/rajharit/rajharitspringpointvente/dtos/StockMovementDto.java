package com.rajharit.rajharitspringpointvente.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockMovementDto {
    private String movementType;
    private double quantity;
    private String unit;
    private LocalDateTime movementDate;
}