package com.rajharit.rajharitsprings.dtos;

import com.rajharit.rajharitsprings.entities.StatusType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDishDto {
    private int dishId;
    private String dishName;
    private double dishPrice;
    private int quantity;
    private StatusType status;
}