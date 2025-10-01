package com.ecommerce;

import com.ecommerce.product.catalog.ProductCatalog;
import com.ecommerce.cart.Cart;
import com.ecommerce.product.*;
import com.ecommerce.product.decorators.*;
import com.ecommerce.payment.*;

import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final ProductCatalog catalog = new ProductCatalog();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== E-Commerce System ===");
            System.out.println("1. Owner/Admin Mode");
            System.out.println("2. Customer Mode");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> ownerMode(scanner);
                case 2 -> customerMode(scanner);
                case 3 -> {
                    running = false;
                    logger.info("Exiting application...");
                }
                default -> System.out.println("Invalid option!");
            }
        }
        scanner.close();
    }

    private static void ownerMode(Scanner scanner) {
        boolean adding = true;
        while (adding) {
            System.out.println("\nAdd product type:");
            System.out.println("1. Single Product");
            System.out.println("2. Product Bundle");
            System.out.print("Choose type: ");
            int typeChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            Product product = null;

            if (typeChoice == 1) {
                System.out.print("Enter product name: ");
                String name = scanner.nextLine();
                System.out.print("Enter product price: ");
                double price = scanner.nextDouble();
                scanner.nextLine();
                product = new SingleProduct(name, price);

            } else if (typeChoice == 2) {
                System.out.print("Enter bundle name: ");
                String bundleName = scanner.nextLine();
                product = new ProductBundle(bundleName);

                boolean addingBundleProducts = true;
                while (addingBundleProducts) {
                    System.out.print("Enter product name for bundle: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter product price: ");
                    double price = scanner.nextDouble();
                    scanner.nextLine();
                    Product bundleItem = new SingleProduct(name, price);

                    // Optional decorators for bundle items
                    System.out.print("Add discount decorator to this item? (y/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        System.out.print("Enter discount percentage: ");
                        double discount = scanner.nextDouble();
                        scanner.nextLine();
                        bundleItem = new DiscountDecorator(bundleItem, discount);
                    }

                    System.out.print("Add gift wrap decorator to this item? (y/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        bundleItem = new GiftWrapDecorator(bundleItem);
                    }

                    product.addProduct(bundleItem);

                    System.out.print("Add another product to this bundle? (y/n): ");
                    addingBundleProducts = scanner.nextLine().equalsIgnoreCase("y");
                }
            } else {
                System.out.println("Invalid choice.");
                continue;
            }

            // Apply decorators for the whole product (single or bundle)
            System.out.print("Add discount decorator to this product? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.print("Enter discount percentage: ");
                double discount = scanner.nextDouble();
                scanner.nextLine();
                product = new DiscountDecorator(product, discount);
            }

            System.out.print("Add gift wrap decorator to this product? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                product = new GiftWrapDecorator(product);
            }

            // Always apply tax decorator
            product = new TaxDecorator(product, 18);

            catalog.addProduct(product);

            System.out.print("Add another product? (y/n): ");
            adding = scanner.nextLine().equalsIgnoreCase("y");
        }
    }

    private static void customerMode(Scanner scanner) {
        Cart cart = new Cart();
        boolean shopping = true;

        while (shopping) {
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1. View Products");
            System.out.println("2. Add Product to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Remove Product from Cart");
            System.out.println("5. Checkout");
            System.out.println("6. Exit Customer Mode");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> catalog.showCatalog();
                case 2 -> {
                    catalog.showCatalog();
                    System.out.print("Enter product number to add: ");
                    int productIndex = scanner.nextInt() - 1;
                    scanner.nextLine();
                    Product product = catalog.getProduct(productIndex);
                    if (product != null) {
                        cart.addProduct(product);
                    }
                }
                case 3 -> cart.showCart();
                case 4 -> {
                    cart.showCart();
                    System.out.print("Enter product number to remove: ");
                    int removeIndex = scanner.nextInt() - 1;
                    scanner.nextLine();
                    cart.removeProduct(removeIndex);
                }
                case 5 -> {
                    if (cart.isEmpty()) {
                        System.out.println("Cart is empty! Add products first.");
                        break;
                    }
                    boolean decorating = true;

                    while (decorating) {
                        System.out.println("\nDo you want to add a decoration to a product? (y/n): ");
                        String decorateChoice = scanner.nextLine();

                        if (decorateChoice.equalsIgnoreCase("y")) {
                            cart.showCart();

                            System.out.print("Enter product number to decorate: ");
                            int productIndex = scanner.nextInt();
                            scanner.nextLine(); // consume newline

                            Product selectedProduct = cart.getProduct(productIndex - 1);

                            System.out.println("Available Decorations:");
                            System.out.println("1. Gift Wrap (+$5)");
                            System.out.println("2. Engraving (+$10)");
                            System.out.print("Choose decoration: ");
                            int decorChoice = scanner.nextInt();
                            scanner.nextLine(); // consume newline

                            switch (decorChoice) {
                                case 1 -> selectedProduct = new GiftWrapDecorator(selectedProduct);
                                case 2 -> selectedProduct = new EngravingDecorator(selectedProduct);
                                default -> System.out.println("Invalid decoration choice!");
                            }

                            cart.updateProduct(productIndex - 1, selectedProduct);
                            System.out.println("Decoration applied!");
                        } else {
                            decorating = false;
                        }
                    }

                    double total = cart.getTotal();
                    System.out.println("Total amount: $" + total);

                    System.out.println("\nChoose Payment Method:");
                    System.out.println("1. Credit Card");
                    System.out.println("2. PayPal");
                    System.out.println("3. UPI");
                    int payChoice = scanner.nextInt();

                    Payment payment = PaymentFactory.createPayment(
                            switch (payChoice) {
                                case 1 -> "CREDITCARD";
                                case 2 -> "PAYPAL";
                                case 3 -> "UPI";
                                default -> throw new IllegalArgumentException("Invalid payment option");
                            }
                    );

                    payment.pay(total);
                    cart.clear();
                }
                case 6 -> shopping = false;
                default -> System.out.println("Invalid option!");
            }
        }
    }
}
