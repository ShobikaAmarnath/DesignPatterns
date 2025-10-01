package com.ecommerce.product.decorators;

import com.ecommerce.product.Product;

public class DiscountDecorator extends ProductDecorator {
    private final double discountPercentage;

    public DiscountDecorator(Product product, double discountPercentage) {
        super(product);
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Invalid discount percentage");
        }
        this.discountPercentage = discountPercentage;
    }

    @Override
    public double getPrice() {
        return super.getPrice() * (1 - discountPercentage / 100);
    }

    @Override
    public void showDetails() {
        super.showDetails();
        System.out.println(" - Discount (" + discountPercentage + "%)");
    }
}
