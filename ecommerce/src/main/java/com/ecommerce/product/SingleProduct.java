package com.ecommerce.product;

// Leaf
public class SingleProduct implements Product {
    private final String name;
    private final double price;

    public SingleProduct(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void showDetails() {
        System.out.println("Product: " + name + " | Price: $" + price);
    }
}
