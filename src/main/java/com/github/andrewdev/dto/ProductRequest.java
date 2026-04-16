package com.github.andrewdev.dto;

import java.math.BigDecimal;

public class ProductRequest {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;

    public ProductRequest() {}

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    @Override
    public String toString() {
        return String.format("ID: %s, Name: %s, Price: %s, Quantity: %s", 
        id, name, price, quantity);
    }

    public void validate() {
        if (this.price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Provided product price cannot be a negative value.");
        }
        if (this.quantity < 0) {
            throw new IllegalArgumentException("Provided product quantity cannot be a negative value.");
        }
        if (this.name == null) {
            throw new IllegalArgumentException("Provided product name cannot be a empty.");
        }
    }
}
