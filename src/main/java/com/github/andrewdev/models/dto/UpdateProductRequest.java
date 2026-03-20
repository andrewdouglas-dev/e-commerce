package com.github.andrewdev.models.dto;

import java.math.BigDecimal;

public class UpdateProductRequest {
    private final long id;
    private final String name;
    private final BigDecimal price;
    private final int quantity;

    public UpdateProductRequest(long id, String name) {
        this.id = id;
        this.name = name;
        this.price = null;
        this.quantity = 0;
    }

    public UpdateProductRequest(long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = 0;
    }

    public UpdateProductRequest(long id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.price = null;
        this.quantity = quantity;
    }

    public UpdateProductRequest(long id, String name, BigDecimal price, int quantity) { 
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public long getId() {
        return this.id;
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
