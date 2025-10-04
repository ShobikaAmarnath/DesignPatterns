package com.ecommerce.product;

// Component
public interface Product {
    String getName();
    void showDetails();
    double getPrice();

    // Default: only composites override this
    default void addProduct(Product product) {
        throw new UnsupportedOperationException("Cannot add product to a single product");
    }
}
