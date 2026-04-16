package com.github.andrewdev.mapper;

import com.github.andrewdev.dto.ProductRequest;
import com.github.andrewdev.models.Product;

public class ProductMapper {
    private ProductMapper(){}

    public static void updateProduct(ProductRequest request, Product existingProduct) {
        if (request.getName() != null) {
            existingProduct.setName(request.getName());
        }
        if (request.getPrice() != null) {
            existingProduct.setPrice(request.getPrice());
        }
        if (request.getQuantity() != null) {
            existingProduct.setQuantity(request.getQuantity());
        }
    }

    public static Product convertToNewProduct(ProductRequest request) {
        return new Product(
            request.getName(),
            request.getPrice(),
            request.getQuantity());
    }
}
