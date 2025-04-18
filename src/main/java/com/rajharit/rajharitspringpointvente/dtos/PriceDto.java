package com.rajharit.rajharitspringpointvente.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PriceDto {
    private double price;
    private LocalDateTime date;
}
