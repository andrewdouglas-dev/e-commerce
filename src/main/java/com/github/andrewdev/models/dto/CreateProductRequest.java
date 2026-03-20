package com.github.andrewdev.models.dto;

import java.math.BigDecimal;

public class CreateProductRequest {
    private final String name;
    private final BigDecimal price;
    private final int quantity;

    public CreateProductRequest(String name, BigDecimal price, int quantity) { 
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
