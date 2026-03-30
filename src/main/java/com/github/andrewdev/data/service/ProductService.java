package com.github.andrewdev.data.service;

import java.util.List;
import java.util.Optional;

import com.github.andrewdev.data.dao.ProductDAO;
import com.github.andrewdev.data.dao.impl.ProductDAOImpl;
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

    public void update(Product request) {
        productDAO.update(request);
    }

    public void delete(Long id) {
        productDAO.delete(id);
    }
}
