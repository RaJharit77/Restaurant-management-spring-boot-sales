package com.rajharit.rajharitsprings.dtos;

import com.rajharit.rajharitsprings.entities.StatusType;
import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateDto {
    private StatusType orderStatus;
    private List<OrderDishUpdateDto> dishes;
}