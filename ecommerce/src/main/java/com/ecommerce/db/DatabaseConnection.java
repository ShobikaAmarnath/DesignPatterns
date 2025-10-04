package com.ecommerce.db;

import com.ecommerce.product.Product;
import com.ecommerce.order.Order;

import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;

    // Simulated DB tables
    private final List<Product> productTable = new ArrayList<>();
    private final List<Order> orderTable = new ArrayList<>();

    // Private constructor for Singleton
    private DatabaseConnection() {
        System.out.println("\n💾 Database Connection Created (Singleton Instance)!");
    }

    // Get the single instance
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    // ------------------- Product Operations -------------------

    public void saveProduct(Product product) {
        if (product == null) {
            System.out.println("⚠️ Cannot save null product to database.");
            return;
        }
        productTable.add(product);
        System.out.println("📦 Product '" + product.getName() + "' saved successfully to the database.");
    }

    public List<Product> getProducts() {
        return new ArrayList<>(productTable);
    }

    // ------------------- Order Operations -------------------

    public void saveOrder(Order order) {
        if (order == null) {
            System.out.println("⚠️ Cannot save null order to database.");
            return;
        }
        orderTable.add(order);
        System.out.println("🧾 Order '" + order.getOrderId() + "' saved successfully to the database.");
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orderTable);
    }

    // ------------------- Utility -------------------

    public void showSummary() {
        System.out.println("\n=== 🗄️ Database Summary ===");
        System.out.println("Products stored: " + productTable.size());
        System.out.println("Orders stored: " + orderTable.size());
        System.out.println("----------------------------");
    }
}
