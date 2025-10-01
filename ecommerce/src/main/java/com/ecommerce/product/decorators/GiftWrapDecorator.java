package com.ecommerce.product.decorators;

import com.ecommerce.product.Product;

public class GiftWrapDecorator extends ProductDecorator {
    public GiftWrapDecorator(Product product) {
        super(product);
    }

    @Override
    public double getPrice() {
        return super.getPrice() + 5.0;
    }

    @Override
    public void showDetails() {
        super.showDetails();
        System.out.println(" + Gift Wrap ($5)");
    }
}
