package com.github.andrewdev.models;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;

    public Product(String name, BigDecimal price, int quantity) {
        this.id = null;
        setName(name);
        setPrice(price);
        setQuantity(quantity);
    }

    public Product(Long id, String name, BigDecimal price, int quantity) {
        setId(id);
        setName(name);
        setPrice(price);
        setQuantity(quantity);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id != null) {
            return;
        }

        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Provided product name cannot be null or blank.");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Provided product price cannot be a negative value.");
        }

        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Provided product quantity cannot be a negative value.");
        }

        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void reduceQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Provided amount cannot be a negative value.");
        }

        if (amount > this.quantity) {
            throw new IllegalArgumentException("Provided amount cannot be greater than the current quantitiy.");
        }

        quantity -= amount;
    }

    public boolean hasStock() {
        return quantity > 0;
    }

    @Override
    public String toString() {
        return String.format("ID: %s, Name: %s, Price: %s, Quantity: %s", 
        id, name, price, quantity);
    }
}
