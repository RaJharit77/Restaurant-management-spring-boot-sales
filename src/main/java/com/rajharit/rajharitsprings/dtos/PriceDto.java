package com.rajharit.rajharitsprings.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PriceDto {
    private int id;
    private double price;
    private LocalDateTime date;
}
