package com.ecommerce.product.decorators;

import com.ecommerce.product.Product;

public class EngravingDecorator extends ProductDecorator {
    private final double engravingCost = 10.0;
    private final String text;

    public EngravingDecorator(Product product, String text) {
        super(product);
        this.text = (text == null ? "" : text);
    }

    @Override
    public void showDetails() {
        product.showDetails();
        System.out.println("    + Engraving: \"" + text + "\" ($" + String.format("%.2f", engravingCost) + ")");
    }

    @Override
    public double getPrice() {
        return product.getPrice() + engravingCost;
    }
}
