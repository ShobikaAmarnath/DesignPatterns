package com.ecommerce.product.decorators;

import com.ecommerce.product.Product;

public class TaxDecorator extends ProductDecorator {
    private final double taxPercent;

    public TaxDecorator(Product product, double taxPercent) {
        super(product);
        if (taxPercent < 0) throw new IllegalArgumentException("Invalid tax %");
        this.taxPercent = taxPercent;
    }

    @Override
    public void showDetails() {
        product.showDetails();
        System.out.println("    + Tax (" + String.format("%.2f", taxPercent) + "%)");
        System.out.println("    Price after tax: $" + String.format("%.2f", getPrice()));
    }

    @Override
    public double getPrice() {
        return product.getPrice() * (1 + taxPercent / 100.0);
    }
}
