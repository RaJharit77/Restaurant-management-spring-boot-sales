package com.rajharit.rajharitsprings.dtos;

import com.rajharit.rajharitsprings.entities.MovementType;
import com.rajharit.rajharitsprings.entities.Unit;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockMovementDto {
    private int id;
    private MovementType movementType;
    private double quantity;
    private Unit unit;
    private LocalDateTime movementDate;
}