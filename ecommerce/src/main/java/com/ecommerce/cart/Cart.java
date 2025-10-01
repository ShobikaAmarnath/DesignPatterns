package com.ecommerce.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ecommerce.product.Product;

public class Cart {
    private static final Logger logger = Logger.getLogger(Cart.class.getName());
    private final List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Cannot add null product to cart");
        }
        products.add(product);
        logger.info("Product added to cart.");
    }

    public void removeProduct(int index) {
        if (index < 0 || index >= products.size()) {
            throw new IllegalArgumentException("Invalid product index");
        }
        Product removed = products.remove(index);
        logger.info("Removed from cart: " + removed);
    }

    public Product getProduct(int index) {
        return products.get(index);
    }

    public void updateProduct(int index, Product product) {
        products.set(index, product);
    }

    public double getTotal() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }

    public void showCart() {
        if (products.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("\n=== Your Cart ===");
        int index = 1;
        for (Product product : products) {
            System.out.print(index++ + ". ");
            product.showDetails();
            System.out.println("Price: $" + product.getPrice());
            System.out.println("----------------------");
        }
        System.out.println("Total Cart Value: $" + getTotal());
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public void clear() {
        products.clear();
    }

}
