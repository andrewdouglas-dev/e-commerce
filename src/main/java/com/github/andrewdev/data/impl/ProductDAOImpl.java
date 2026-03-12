package com.github.andrewdev.data.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrewdev.data.dao.ProductDAO;
import com.github.andrewdev.models.Product;

public class ProductDAOImpl implements  ProductDAO{
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(ProductDAOImpl.class.getName());

    public ProductDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Product> findById(Long id) {
        String statement = "SELECT * FROM products WHERE id = ?";

        try {
            PreparedStatement pStatement = connection.prepareStatement(statement);
            pStatement.setLong(1, id);
            ResultSet rs = pStatement.executeQuery();

            if (rs.next()) {
                Product newProduct = new Product(
                    (Long) rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );

                return Optional.of(newProduct);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Exception while executing Product findById Query. {0}", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        String statement = "SELECT * FROM products";
        List<Product> products = new ArrayList<>();

        try {
            PreparedStatement pStatement = connection.prepareStatement(statement);
            ResultSet rs = pStatement.executeQuery();

            while (rs.next()) {
                Product newProduct = new Product(
                    (Long) rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );

                products.add(newProduct);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Exception while executing Product findById Query. {0}", e);
        }

        return products;
    }

    @Override
    public void update(Product p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(Product p) {
        String statement = "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)";

        try {
            PreparedStatement pStatement = connection.prepareStatement(statement);
            pStatement.setString(1, p.getName());
            pStatement.setDouble(2, p.getPrice());
            pStatement.setInt(3, p.getQuantity());
            
            boolean recordInserted = pStatement.executeUpdate() == 1;

            if (recordInserted) {
                logger.info("Successfully inserted a record to Products table");
            } else {
                logger.info("Insert into products table failed.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Insert failed: {0}", e);
        }
    }
}
