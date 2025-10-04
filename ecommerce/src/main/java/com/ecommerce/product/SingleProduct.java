package com.ecommerce.product;

public class SingleProduct implements Product {
    private final String name;
    private final double price;

    public SingleProduct(String name, double price) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be null/blank");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.name = name;
        this.price = price;
    }

    @Override
    public String getName() { return name; }

    @Override
    public void showDetails() {
        System.out.println("Product: " + name + " | Price: $" + String.format("%.2f", getPrice()));
    }

    @Override
    public double getPrice() { return price; }
}
