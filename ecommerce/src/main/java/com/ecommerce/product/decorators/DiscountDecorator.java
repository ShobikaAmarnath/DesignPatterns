package com.ecommerce.product.decorators;

import com.ecommerce.product.Product;

public class DiscountDecorator extends ProductDecorator {
    private final double discountPercent;

    public DiscountDecorator(Product product, double discountPercent) {
        super(product);
        if (discountPercent < 0 || discountPercent > 100) throw new IllegalArgumentException("Invalid discount %");
        this.discountPercent = discountPercent;
    }

    @Override
    public void showDetails() {
        product.showDetails();
        System.out.println("    - Discount (" + String.format("%.2f", discountPercent) + "%)");
        System.out.println("    Price after discount: $" + String.format("%.2f", getPrice()));
    }

    @Override
    public double getPrice() {
        return product.getPrice() * (1 - discountPercent / 100.0);
    }
}
