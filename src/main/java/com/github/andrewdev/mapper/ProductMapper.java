package com.github.andrewdev.mapper;

import com.github.andrewdev.dto.ProductRequest;
import com.github.andrewdev.models.Product;

public class ProductMapper {
    private ProductMapper(){}

    public static Product convertToProduct(ProductRequest request) {
        return new Product(
            request.getId(),
            request.getName(),
            request.getPrice(),
            request.getQuantity()
        );
    }
}
