package com.ecommerce.product;

import java.util.ArrayList;
import java.util.List;

public class ProductBundle implements Product {
    private final String bundleName;
    private final List<Product> products = new ArrayList<>();

    public ProductBundle(String bundleName) {
        if (bundleName == null || bundleName.isBlank()) throw new IllegalArgumentException("Bundle name cannot be empty");
        this.bundleName = bundleName;
    }

    @Override
    public String getName() { return bundleName; }

    @Override
    public void addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Cannot add null product to bundle");
        products.add(product);
    }

    @Override
    public void showDetails() {
        System.out.println("Bundle: " + bundleName);
        for (Product p : products) {
            System.out.print("  - ");
            p.showDetails();
        }
        System.out.println("  Total Bundle Price: $" + String.format("%.2f", getPrice()));
    }

    @Override
    public double getPrice() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }
}
