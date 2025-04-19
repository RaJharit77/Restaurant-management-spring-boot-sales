package com.rajharit.rajharitsprings.entities;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DishOrderStatus {
    private int dishOrderStatusId;
    private StatusType status;
    private LocalDateTime changedAt;

    public DishOrderStatus(int dishOrderStatusId, StatusType status, LocalDateTime changedAt) {
        this.dishOrderStatusId = dishOrderStatusId;
        this.status = status;
        this.changedAt = changedAt;
    }

    public DishOrderStatus() {
    }
}