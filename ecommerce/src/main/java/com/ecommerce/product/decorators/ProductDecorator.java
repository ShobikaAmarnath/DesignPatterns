package com.ecommerce.product.decorators;

import com.ecommerce.product.Product;

// Base Decorator
public abstract class ProductDecorator implements Product {
    protected Product product;

    public ProductDecorator(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null in decorator");
        }
        this.product = product;
    }

    @Override
    public String getName() {
        return product.getName();
    }

    @Override
    public void showDetails() {
        product.showDetails();
    }

    @Override
    public double getPrice() {
        return product.getPrice();
    } // delegate to wrapped product
}

