package com.github.andrewdev.models;

public class Product {
    private Long id;
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.id = null;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(Long id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null) {
            return;
        }

        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Provided product name cannot be null or blank");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Provided product price cannot be a negative value");
        }

        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Provided product quantity cannot be a negative value");
        }

        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}
