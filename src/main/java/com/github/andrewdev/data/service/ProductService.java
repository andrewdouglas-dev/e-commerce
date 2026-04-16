package com.github.andrewdev.data.service;

import java.util.List;
import java.util.Optional;

import com.github.andrewdev.data.dao.ProductDAO;
import com.github.andrewdev.data.dao.impl.ProductDAOImpl;
import com.github.andrewdev.dto.ProductRequest;
import com.github.andrewdev.mapper.ProductMapper;
import com.github.andrewdev.models.Product;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAOImpl();
    }

    public Long create(Product request) {
        return productDAO.add(request);
    }

    public Optional<Product> findById(Long id) {
        return productDAO.findById(id);
    }

    public List<Product> findAll() {
        return productDAO.findAll();
    }

    public Product update(ProductRequest request, Long id) {
        Optional<Product> existingProduct = findById(id);

        if (existingProduct.isEmpty()) {
            throw new IllegalArgumentException("No product found with ID: " + id);
        }

        ProductMapper.updateProduct(request, existingProduct.get());

        productDAO.update(existingProduct.get());   
        
        return existingProduct.get();
    }

    public void delete(Long id) {
        if (!exists(id)) {
            throw new IllegalArgumentException("No product found with ID: " + id);
        }

        productDAO.delete(id);
    }

    public boolean exists(Long id){
        return findById(id).isPresent();
    }
}