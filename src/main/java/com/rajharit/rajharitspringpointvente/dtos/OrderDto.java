package com.rajharit.rajharitspringpointvente.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private int orderId;
    private String reference;
    private LocalDateTime createdAt;
    private String status;
    private double totalAmount;
    private List<OrderDishDto> dishes;
}
