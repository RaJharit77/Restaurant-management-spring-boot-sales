package com.rajharit.rajharitsprings.entities;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class StockMovement {
    private int movementId;
    private int ingredientId;
    private MovementType movementType;
    private double quantity;
    private Unit unit;
    private LocalDateTime movementDate;

    public StockMovement(int movementId, int ingredientId, MovementType movementType, double quantity, Unit unit, LocalDateTime movementDate) {
        this.movementId = movementId;
        this.ingredientId = ingredientId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.unit = unit;
        this.movementDate = movementDate;
    }
}