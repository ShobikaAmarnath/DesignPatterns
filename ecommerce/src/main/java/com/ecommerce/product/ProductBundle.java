package com.ecommerce.product;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProductBundle implements Product {
    private static final Logger logger = Logger.getLogger(ProductBundle.class.getName());

    private final String name;
    private final List<Product> products = new ArrayList<>();

    public ProductBundle(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Bundle name cannot be null or empty");
        }
        this.name = name;
    }

    public void addProduct(Product product) {
        if (product == null) {
            logger.warning("Attempted to add null product to bundle: " + name);
            throw new IllegalArgumentException("Product cannot be null");
        }
        products.add(product);
        logger.info("Added product to bundle: " + name);
    }

    @Override
    public double getPrice() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }

    @Override
    public void showDetails() {
        logger.info("Bundle: " + name);
        for (Product product : products) {
            product.showDetails();
        }
        logger.info("Total Price of Bundle: $" + getPrice());
    }
}
