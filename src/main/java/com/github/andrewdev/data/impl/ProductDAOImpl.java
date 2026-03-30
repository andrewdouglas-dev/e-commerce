package com.github.andrewdev.data.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrewdev.data.DatabaseManager;
import com.github.andrewdev.data.dao.ProductDAO;
import com.github.andrewdev.models.Product;

public class ProductDAOImpl implements  ProductDAO{
    private static final Logger logger = Logger.getLogger(ProductDAOImpl.class.getName());

    @Override
    public Optional<Product> findById(Long id) {
        String statement = "SELECT * FROM products WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement)){
            pStatement.setLong(1, id);

            try (ResultSet rs = pStatement.executeQuery()){
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occured while finding product by ID: " + id,e);
        }

        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        String statement = "SELECT * FROM products";
        List<Product> products = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement);
            ResultSet rs = pStatement.executeQuery();){

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "test1", e);
            throw new RuntimeException("Error occured while finding all products",e);
        }

        return products;
    }

    @Override
    public void update(Product p) {
        if (!validateIDExists(p.getId())) {
            throw new IllegalArgumentException("No product found with ID: " + p.getId());
        }

        String statement = "UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement)){

            if (p.getName() != null) {
                pStatement.setString(1, p.getName());
            }
            if (p.getPrice() != null) {
                pStatement.setBigDecimal(2, p.getPrice());
            }
            if (p.getQuantity() != null) {
                pStatement.setInt(3, p.getQuantity());
            }

            pStatement.setLong(4, p.getId());

            boolean recordUpdated = pStatement.executeUpdate() == 1;

            if (!recordUpdated) {
                throw new RuntimeException("No product found with ID: " + p.getId());
            }
        } catch (Exception e){
            throw new RuntimeException("Error occured while updating product with ID: " + p.getId());
        }
    }

    @Override
    public void delete(Long id) {
        if (!validateIDExists(id)) {
            throw new IllegalArgumentException("No product found with ID: " + id);
        }

        String statement = "DELETE FROM products WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement)) {

            pStatement.setLong(1, id);

            boolean recordDeleted = pStatement.executeUpdate() == 1;

            if (!recordDeleted) {
                throw new RuntimeException("No product found with ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occured while deleted product with ID: " + id);
        }
    }

    @Override
    public Long add(Product p) {
        if (p == null) {
            throw new NullPointerException("Provided product cannot be null");
        }

        String statement = "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
            pStatement.setString(1, p.getName());
            pStatement.setBigDecimal(2, p.getPrice());
            pStatement.setInt(3, p.getQuantity());

            boolean recordInserted = pStatement.executeUpdate() == 1;

            if (!recordInserted) {
                throw new RuntimeException("Error occured while adding product: " + p.getName());
            }

            try(ResultSet generatedKey = pStatement.getGeneratedKeys()) {
                if (generatedKey.next()) {
                    logger.info("Successfully inserted a record to Products table");
                    Long newId = generatedKey.getLong(1);

                    p.setId(newId);

                    return newId;
                } else {
                    throw new SQLException("Failed to retrieve generated ID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occured while adding product: " + p.getName(),e);
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
            (Long) rs.getLong("id"),
            rs.getString("name"),
            rs.getBigDecimal("price"),
            rs.getInt("quantity")
        );
    }

    private boolean validateIDExists(Long id) {
        Optional<Product> existingProduct = findById(id);

        return existingProduct.isPresent();
    }
}
