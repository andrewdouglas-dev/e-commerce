package com.github.andrewdev.data.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.github.andrewdev.data.DatabaseManager;
import com.github.andrewdev.data.dao.ProductDAO;
import com.github.andrewdev.models.Product;

public class ProductDAOImpl implements  ProductDAO{
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(ProductDAOImpl.class.getName());

    public ProductDAOImpl() {
        try {
            this.connection = DatabaseManager.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection",e);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        String statement = "SELECT * FROM products WHERE id = ?";

        try (PreparedStatement pStatement = connection.prepareStatement(statement)){
            pStatement.setLong(1, id);

            try (ResultSet rs = pStatement.executeQuery()){
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occured while finding product by ID: " + id,e);
        }

        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        String statement = "SELECT * FROM products";
        List<Product> products = new ArrayList<>();

        try (PreparedStatement pStatement = connection.prepareStatement(statement);
            ResultSet rs = pStatement.executeQuery();){

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occured while finding all products",e);
        }

        return products;
    }

    @Override
    public void update(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Long id) {
        String statement = "DELETE FROM products WHERE id = ?";

        try (PreparedStatement pStatement = connection.prepareStatement(statement)) {
            boolean recordDeleted = pStatement.executeUpdate() == 1;

            if (!recordDeleted) {
                throw new RuntimeException("No product found with ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occured while deleted product with ID: " + id);
        }
    }

    @Override
    public void add(Product p) {
        String statement = "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)";

        try (PreparedStatement pStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
            pStatement.setString(1, p.getName());
            pStatement.setDouble(2, p.getPrice());
            pStatement.setInt(3, p.getQuantity());

            boolean recordInserted = pStatement.executeUpdate() == 1;

            if (recordInserted) {
                try(ResultSet generatedKey = pStatement.getGeneratedKeys()) {
                    if (generatedKey.next()) {
                        Long newId = generatedKey.getLong(1);
                        p.setId(newId);
                        logger.info("Successfully inserted a record to Products table");
                    } else {
                        throw new SQLException("Failed to retrieve generated ID");
                    }
                }
            } else {
                throw new RuntimeException("Error occured while adding product: " + p.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occured while adding product: " + p.getName(),e);
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
            (Long) rs.getLong("id"),
            rs.getString("name"),
            rs.getDouble("price"),
            rs.getInt("quantity")
        );
    }
}
