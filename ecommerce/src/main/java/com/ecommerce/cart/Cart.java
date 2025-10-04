package com.ecommerce.cart;

import com.ecommerce.product.Product;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<Product> products = new ArrayList<>();

    // Add a product to the cart
    public void addProduct(Product product) {
        if (product == null) {
            System.out.println("⚠️ Cannot add an empty product to the cart.");
            return;
        }
        products.add(product);
        System.out.println("🛍️ '" + product.getName() + "' added to your cart.");
    }

    // Remove product by index
    public void removeProduct(int index) {
        if (index < 0 || index >= products.size()) {
            System.out.println("⚠️ Invalid item number. Please choose a valid product to remove.");
            return;
        }
        Product removed = products.remove(index);
        System.out.println("🗑️ Removed '" + removed.getName() + "' from your cart.");
    }

    // Retrieve product safely
    public Product getProduct(int index) {
        if (index < 0 || index >= products.size()) {
            throw new IllegalArgumentException("Invalid product selection from cart.");
        }
        return products.get(index);
    }

    // Update product (e.g., apply decorator)
    public void updateProduct(int index, Product updatedProduct) {
        if (index < 0 || index >= products.size()) {
            System.out.println("⚠️ Invalid product number. Update failed.");
            return;
        }
        products.set(index, updatedProduct);
        System.out.println("✨ '" + updatedProduct.getName() + "' updated successfully.");
    }

    // Calculate total cost
    public double getTotal() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }

    // Display cart contents
    public void showCart() {
        if (products.isEmpty()) {
            System.out.println("\n🛒 Your cart is empty. Add some products to get started!");
            return;
        }

        System.out.println("\n=== 🧾 Your Shopping Cart ===");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            System.out.println((i + 1) + ". " + p.getName());
            p.showDetails();
            System.out.printf("   💲 Price: $%.2f%n", p.getPrice());
            System.out.println("----------------------------");
        }
        System.out.printf("🧮 Total Cart Value: $%.2f%n", getTotal());
    }

    // Check if cart is empty
    public boolean isEmpty() {
        return products.isEmpty();
    }

    // Clear all products from cart
    public void clear() {
        if (products.isEmpty()) {
            System.out.println("🧺 Cart is already empty.");
        } else {
            products.clear();
            System.out.println("✅ All items cleared from your cart.");
        }
    }

    // Return a safe copy of cart items
    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }
}
