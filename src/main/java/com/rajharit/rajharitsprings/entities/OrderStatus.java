package com.rajharit.rajharitsprings.entities;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class OrderStatus {
    private int orderStatusId;
    private StatusType status;
    private LocalDateTime changedAt;

    public OrderStatus(int orderStatusId, StatusType status, LocalDateTime changedAt) {
        this.orderStatusId = orderStatusId;
        this.status = status;
        this.changedAt = changedAt;
    }

    public OrderStatus() {
    }
}