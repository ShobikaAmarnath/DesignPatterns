package com.ecommerce.product.decorators;

import com.ecommerce.product.Product;

public class GiftWrapDecorator extends ProductDecorator {
    private final double wrapCost = 5.0;

    public GiftWrapDecorator(Product product) {
        super(product);
    }

    @Override
    public void showDetails() {
        product.showDetails();
        System.out.println("    + Gift Wrap ($" + String.format("%.2f", wrapCost) + ")");
    }

    @Override
    public double getPrice() {
        return product.getPrice() + wrapCost;
    }
}
