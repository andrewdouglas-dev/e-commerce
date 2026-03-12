package com.github.andrewdev.data.seeders;

import java.sql.SQLException;
import java.util.logging.Logger;

import com.github.andrewdev.data.DatabaseManager;
import com.github.andrewdev.data.dao.ProductDAO;
import com.github.andrewdev.data.impl.ProductDAOImpl;
import com.github.andrewdev.models.Product;

import net.datafaker.Faker;

public class ProductSeeder {
    private static final Logger logger = Logger.getLogger(ProductSeeder.class.getName());
    private static final Faker faker = new Faker();
    private static final int TOTAL_PRODUCTS = 100;

    public static void main(String[] args) {
        try {
            ProductDAO productDAO = new ProductDAOImpl(DatabaseManager.getConnection());

            logger.info("Starting Product Seeder");

            for (int i = 0; i < TOTAL_PRODUCTS; i++) {
                Product p = new Product(
                    faker.commerce().productName(), 
                    Double.parseDouble(faker.commerce().price()),
                    faker.number().numberBetween(0, 1000)
                );

                productDAO.add(p);

                logger.info("Insert Record (" + i + "), name: " + p.getName() + " price: " + p.getPrice() + " quantity: " + p.getQuantity());
            }

            logger.info("Successfully seeded " + TOTAL_PRODUCTS + " products.");
        } catch (SQLException ex) {
            System.getLogger(ProductSeeder.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
