package com.rajharit.rajharitsprings.dtos;

import lombok.Data;

@Data
public class DishSoldDto {
    private int dishIdentifier;
    private String dishName;
    private int quantitySold;
}