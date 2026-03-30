package com.github.andrewdev.data.seeders;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrewdev.data.dao.ProductDAO;
import com.github.andrewdev.data.dao.impl.ProductDAOImpl;
import com.github.andrewdev.models.Product;

import net.datafaker.Faker;

public class ProductSeeder {
    private static final Logger logger = Logger.getLogger(ProductSeeder.class.getName());
    private static final Faker faker = new Faker();
    private static final int TOTAL_PRODUCTS = 100;

    public static void main(String[] args) {
        ProductDAO productDAO = new ProductDAOImpl();

        logger.info("Starting Product Seeder");

        for (int i = 0; i < TOTAL_PRODUCTS; i++) {
            Product p = new Product(
                faker.commerce().productName(), 
                BigDecimal.valueOf(Double.parseDouble(faker.commerce().price())),
                faker.number().numberBetween(0, 1000)
            );

            productDAO.add(p);

            logger.log(Level.INFO, "Insert Record ({0}), name: {1} price: {2} quantity: {3}", new Object[]{i, p.getName(), p.getPrice(), p.getQuantity()});
        }

        logger.log(Level.INFO, "Successfully seeded {0} products.", TOTAL_PRODUCTS);
    }
}
