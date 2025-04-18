package com.rajharit.rajharitspringpointvente.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDishDto {
    private int dishId;
    private String dishName;
    private double dishPrice;
    private int quantity;
    private String status;

    @Data
    public static class BestSalesDto {
        private String dishName;
        private int quantitySold;
        private double totalAmount;
        private LocalDateTime lastUpdated;
    }
}
