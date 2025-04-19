package com.rajharit.rajharitsprings.dtos;

import lombok.Data;

@Data
public class OrderDishUpdateDto {
    private int dishIdentifier;
    private int quantity;
}