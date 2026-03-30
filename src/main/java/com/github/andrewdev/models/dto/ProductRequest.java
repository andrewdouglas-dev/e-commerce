package com.github.andrewdev.models.dto;

import java.math.BigDecimal;

public class ProductRequest {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;

    public ProductRequest() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    @Override
    public String toString() {
        return this.id + " " + this.name + " " + this.price.toString() + " " + this.quantity;
    }
}
