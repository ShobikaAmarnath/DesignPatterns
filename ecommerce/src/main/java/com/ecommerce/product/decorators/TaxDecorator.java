package com.ecommerce.product.decorators;

import com.ecommerce.product.Product;

public class TaxDecorator extends ProductDecorator {
    private final double taxPercentage;

    public TaxDecorator(Product product, double taxPercentage) {
        super(product);
        if (taxPercentage < 0) {
            throw new IllegalArgumentException("Tax percentage cannot be negative");
        }
        this.taxPercentage = taxPercentage;
    }

    @Override
    public double getPrice() {
        return super.getPrice() * (1 + taxPercentage / 100);
    }

    @Override
    public void showDetails() {
        super.showDetails();
        System.out.println(" + Tax (" + taxPercentage + "%)");
    }
}
