package com.rajharit.rajharitsprings.entities;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PriceHistory {
    private double price;
    private LocalDateTime date;

    public PriceHistory(double price, LocalDateTime date) {
        this.price = price;
        this.date = date;
    }
}