package com.ecommerce.product.decorators;

import com.ecommerce.product.Product;

public class EngravingDecorator extends ProductDecorator {
    public EngravingDecorator(Product product) {
        super(product);
    }

    @Override
    public double getPrice() {
        return super.getPrice() + 10.0;
    }

    @Override
    public void showDetails() {
        super.showDetails();
        System.out.println(" + Engraving ($10)");
    }
}
