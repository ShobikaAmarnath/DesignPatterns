package com.ecommerce.product;

// Component
public interface Product {
    double getPrice();
    void showDetails();

    default void addProduct(Product product) {
        throw new UnsupportedOperationException("Cannot add product to a single product");
    }
}
