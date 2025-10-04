package com.ecommerce.catalog;

import com.ecommerce.db.DatabaseConnection;
import com.ecommerce.product.Product;

import java.util.List;

public class ProductCatalog {
    private final DatabaseConnection db = DatabaseConnection.getInstance();

    // Adds a product to the database
    public void addProduct(Product product) {
        db.saveProduct(product);
    }

    // Displays all products in catalog
    public void showCatalog() {
        List<Product> products = db.getProducts();

        if (products.isEmpty()) {
            System.out.println("\n📭 No products have been added yet!");
            return;
        }

        System.out.println("\n=== 🛒 Product Catalog ===");
        int index = 1;
        for (Product product : products) {
            System.out.printf("%d. %s%n", index++, product.getName());
            product.showDetails();
            System.out.printf("💰 Price: $%.2f%n", product.getPrice());
            System.out.println("----------------------------");
        }
    }

    // Retrieves product by index (1-based)
    public Product getProduct(int index) {
        List<Product> products = db.getProducts();
        if (products.isEmpty()) {
            throw new IllegalArgumentException("No products available. Please ask admin to add some first.");
        }
        if (index <= 0 || index > products.size()) {
            throw new IllegalArgumentException("Invalid product number. Please select a valid item from the catalog.");
        }
        return products.get(index - 1);
    }
}
