package com.ecommerce.product.catalog;

import com.ecommerce.product.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductCatalog {
    private final List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public void showCatalog() {
        if (products.isEmpty()) {
            System.out.println("No products in catalog yet.");
            return;
        }

        System.out.println("\n=== Product Catalog ===");
        for (int i = 0; i < products.size(); i++) {
            System.out.print((i + 1) + ". ");
            products.get(i).showDetails();
            System.out.println("Price: $" + products.get(i).getPrice());
            System.out.println("----------------------");
        }
    }

    public Product getProduct(int index) {
        if (index < 0 || index >= products.size()) {
            throw new IllegalArgumentException("Invalid product selection");
        }
        return products.get(index);
    }

    public List<Product> getProducts() {
        return products;
    }
}
