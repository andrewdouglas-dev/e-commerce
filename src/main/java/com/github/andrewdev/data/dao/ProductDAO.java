package com.github.andrewdev.data.dao;

import java.util.List;
import java.util.Optional;

import com.github.andrewdev.models.Product;

public interface ProductDAO {
    public Long add(Product P);
    public Optional<Product> findById(Long id);
    public List<Product> findAll();
    public void update(Product P);
    public void delete(Long id);
}
