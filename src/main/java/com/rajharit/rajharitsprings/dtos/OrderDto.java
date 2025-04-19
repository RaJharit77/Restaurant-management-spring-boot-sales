package com.rajharit.rajharitsprings.dtos;

import com.rajharit.rajharitsprings.entities.StatusType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private int orderId;
    private String reference;
    private LocalDateTime createdAt;
    private StatusType actualStatus;
    private double totalAmount;
    private List<DishOrderDto> dishOrders;
}