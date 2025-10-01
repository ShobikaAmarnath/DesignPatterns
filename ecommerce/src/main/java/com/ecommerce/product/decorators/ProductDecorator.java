package com.ecommerce.product.decorators;

import com.ecommerce.product.Product;

// Base Decorator
public abstract class ProductDecorator implements Product {
    protected Product product; // the object being decorated

    public ProductDecorator(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null in decorator");
        }
        this.product = product;
    }

    @Override
    public double getPrice() {
        return product.getPrice(); // default behavior (can be extended)
    }

    @Override
    public void showDetails() {
        product.showDetails(); // delegate to wrapped product
    }
}
