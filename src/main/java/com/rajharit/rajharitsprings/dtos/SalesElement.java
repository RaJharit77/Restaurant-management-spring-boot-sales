package com.rajharit.rajharitsprings.dtos;

import lombok.Data;

@Data
public class SalesElement {
    private String salesPoint;
    private String dish;
    private Long quantitySold;
    private Double totalAmount;
}
