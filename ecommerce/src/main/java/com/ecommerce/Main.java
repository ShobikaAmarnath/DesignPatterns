package com.ecommerce;

import com.ecommerce.cart.Cart;
import com.ecommerce.catalog.ProductCatalog;
import com.ecommerce.db.DatabaseConnection;
import com.ecommerce.order.Order;
import com.ecommerce.order.OrderService;
import com.ecommerce.order.OrderStatus;
import com.ecommerce.order.observer.AdminObserver;
import com.ecommerce.order.observer.CustomerObserver;
import com.ecommerce.payment.PaymentFactory;
import com.ecommerce.payment.PaymentStrategy;
import com.ecommerce.product.Product;
import com.ecommerce.product.ProductBundle;
import com.ecommerce.product.SingleProduct;
import com.ecommerce.product.decorators.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final ProductCatalog catalog = new ProductCatalog();

    public static void main(String[] args) {
        System.out.println("✅ Database initialized.");
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println("\n=== 🏬 E-Commerce System ===");
                System.out.println("1. Owner/Admin Mode");
                System.out.println("2. Customer Mode");
                System.out.println("3. Exit");
                int choice = readMenuChoice(scanner, "Choose option (1-3): ", 1, 3);
                switch (choice) {
                    case 1 -> ownerMode(scanner);
                    case 2 -> customerMode(scanner);
                    case 3 -> {
                        running = false;
                        System.out.println("👋 Exiting application...");
                    }
                }
            }
        }
    }

    // ---------------- OWNER MODE ----------------
    private static void ownerMode(Scanner scanner) {
        boolean ownerRunning = true;
        while (ownerRunning) {
            System.out.println("\n=== 👑 Owner/Admin Menu ===");
            System.out.println("1. Add Product");
            System.out.println("2. View Product Catalog");
            System.out.println("3. View Orders");
            System.out.println("4. Show Database Summary");
            System.out.println("5. Exit Owner Mode");
            int ownerChoice = readMenuChoice(scanner, "Choose option (1-5): ", 1, 5);

            switch (ownerChoice) {
                case 1 -> {
                    addProductFlow(scanner);
                    DatabaseConnection.getInstance().showSummary();
                }
                case 2 -> catalog.showCatalog();
                case 3 -> showOrders();
                case 4 -> DatabaseConnection.getInstance().showSummary();
                case 5 -> {
                    ownerRunning = false;
                    System.out.println("↩️ Returning to main menu...");
                }
            }
        }
    }

    private static void addProductFlow(Scanner scanner) {
        boolean adding = true;
        while (adding) {
            System.out.println("\nAdd Product Type:");
            System.out.println("1. Single Product");
            System.out.println("2. Product Bundle");
            int type = readMenuChoice(scanner, "Choose type (1 or 2): ", 1, 2);

            Product product = null;
            if (type == 1) {
                // SINGLE PRODUCT
                String name = readNonEmptyString(scanner, "Enter product name: ", "Product name");
                double price = readValidatedDouble(scanner, "Enter product price: ", 0.01, Double.MAX_VALUE, "price");

                product = new SingleProduct(name, price);

                String discountInput = readOptionalString(scanner, "Enter discount percentage (or press Enter to skip): ");
                if (!discountInput.isEmpty()) {
                    try {
                        double discount = Double.parseDouble(discountInput);
                        if (discount < 0 || discount > 100) {
                            System.out.println("⚠️ Discount must be between 0 and 100%. Skipped.");
                        } else {
                            product = new DiscountDecorator(product, discount);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Invalid discount input. Skipped.");
                    }
                }

            } else {
                // BUNDLE
                String bundleName = readNonEmptyString(scanner, "Enter bundle name: ", "Bundle name");
                ProductBundle bundle = new ProductBundle(bundleName);
                int count = 0;

                do {
                    String itemName = readNonEmptyString(scanner, "Enter item name for bundle: ", "Item name");
                    double itemPrice = readValidatedDouble(scanner, "Enter item price: ", 0.01, Double.MAX_VALUE, "price");
                    bundle.addProduct(new SingleProduct(itemName, itemPrice));
                    count++;

                    if (count >= 2) {
                        String more = readOptionalString(scanner, "Add another item? (y/n, default n): ");
                        if (!more.equalsIgnoreCase("y")) break;
                    } else {
                        System.out.println("➡️ You must add at least one more product to make it a bundle.");
                    }
                } while (true);

                String bundleDiscount = readOptionalString(scanner, "Enter bundle discount percentage (or press Enter to skip): ");
                if (!bundleDiscount.isEmpty()) {
                    try {
                        double discount = Double.parseDouble(bundleDiscount);
                        if (discount < 0 || discount > 100) {
                            System.out.println("⚠️ Discount must be between 0 and 100%. Skipped.");
                        } else {
                            product = new DiscountDecorator(bundle, discount);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Invalid discount input. Skipped.");
                    }
                } else {
                    product = bundle;
                }
            }

            // Always apply tax
            product = new TaxDecorator(product, 18);
            catalog.addProduct(product);

            String again = readOptionalString(scanner, "Add another product? (y/n, default n): ");
            if (!again.equalsIgnoreCase("y")) adding = false;
        }
    }

    // ---------------- CUSTOMER MODE ----------------
    private static void customerMode(Scanner scanner) {
        Cart cart = new Cart();
        boolean shopping = true;

        while (shopping) {
            System.out.println("\n=== 🛍️ Customer Menu ===");
            System.out.println("1. View Products");
            System.out.println("2. Add Product to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Remove Product");
            System.out.println("5. Checkout");
            System.out.println("6. Exit Customer Mode");
            int choice = readMenuChoice(scanner, "Choose option (1-6): ", 1, 6);

            switch (choice) {
                case 1 -> catalog.showCatalog();

                case 2 -> {
                    boolean adding = true;
                    while (adding) {
                        catalog.showCatalog();
                        String input = readOptionalString(scanner, "Enter product number to add (or press Enter to stop): ");
                        if (input.isEmpty()) break;

                        try {
                            int idx = Integer.parseInt(input);
                            Product p = catalog.getProduct(idx);
                            cart.addProduct(p);
                            System.out.println("✅ " + p.getName() + " added to cart.");
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ Please enter a valid product number.");
                        } catch (Exception e) {
                            System.out.println("⚠️ " + e.getMessage());
                        }
                    }
                }

                case 3 -> cart.showCart();

                case 4 -> {
                    cart.showCart();
                    if (!cart.isEmpty()) {
                        int rem = readValidatedInt(scanner, "Enter product number to remove: ", 1, cart.getProducts().size(), "product number") - 1;
                        cart.removeProduct(rem);
                        System.out.println("✅ Product removed from cart.");
                    }
                }

                case 5 -> checkoutFlow(scanner, cart);

                case 6 -> {
                    shopping = false;
                    System.out.println("↩️ Returning to main menu...");
                }
            }
        }
    }

    private static void checkoutFlow(Scanner scanner, Cart cart) {
        if (cart.isEmpty()) {
            System.out.println("🛒 Cart is empty! Add products first.");
            return;
        }

        // Decorations
        for (int i = 0; i < cart.getProducts().size(); i++) {
            Product selected = cart.getProduct(i);
            System.out.println("\nDecorate '" + selected.getName() + "'?");
            System.out.println("1. Gift Wrap (+$5)");
            System.out.println("2. Engraving (+$10)");
            System.out.println("3. Skip");
            int decorChoice = readMenuChoice(scanner, "Choose option (1-3): ", 1, 3);

            switch (decorChoice) {
                case 1 -> cart.updateProduct(i, new GiftWrapDecorator(selected));
                case 2 -> {
                    String text = readNonEmptyString(scanner, "Enter engraving text: ", "Engraving text");
                    cart.updateProduct(i, new EngravingDecorator(selected, text));
                }
                case 3 -> System.out.println("➡️ No decoration applied.");
            }
        }

        // Coupon
        String coupon = readOptionalString(scanner, "\nEnter coupon code (or press Enter to skip): ");
        Product orderWrapper = new ProductBundle("Order Items");
        for (Product p : cart.getProducts()) ((ProductBundle) orderWrapper).addProduct(p);

        if (coupon.equalsIgnoreCase("SAVE10")) {
            orderWrapper = new DiscountDecorator(orderWrapper, 10.0);
            System.out.println("🎟️ Coupon applied: 10% off order total!");
        } else if (!coupon.isEmpty()) {
            System.out.println("⚠️ Invalid coupon code.");
        }

        // Payment
        System.out.println("\n--- Final Order ---");
        orderWrapper.showDetails();
        double total = orderWrapper.getPrice();
        System.out.println("💰 Total amount: $" + String.format("%.2f", total));

        System.out.println("\nPayment Methods:");
        System.out.println("1. Credit Card");
        System.out.println("2. PayPal");
        System.out.println("3. UPI");
        int payChoice = readMenuChoice(scanner, "Choose payment method (1-3): ", 1, 3);
        PaymentStrategy payment = PaymentFactory.getPaymentStrategy(payChoice);
        payment.pay(total);

        OrderService orderService = new OrderService();
        Order order = orderService.placeOrder(
                "ORD" + System.currentTimeMillis(),
                new CustomerObserver("Alice"),
                new AdminObserver()
        );
        DatabaseConnection.getInstance().saveOrder(order);
        simulateOrderFlow(order);
        cart.clear();
    }

    // ---------------- ORDER DISPLAY ----------------
    private static void showOrders() {
        var db = DatabaseConnection.getInstance();
        var orders = db.getOrders();

        if (orders.isEmpty()) {
            System.out.println("\n📭 No orders found.");
            return;
        }

        System.out.println("\n=== 📦 All Orders ===");
        for (int i = 0; i < orders.size(); i++) {
            var order = orders.get(i);
            System.out.printf("%d. Order ID: %s | Status: %s%n", i + 1, order.getOrderId(), order.getStatus());
        }
    }

    private static void simulateOrderFlow(Order order) {
        try {
            Thread.sleep(1000);
            order.setStatus(OrderStatus.PAID);

            Thread.sleep(1500);
            order.setStatus(OrderStatus.SHIPPED);

            Thread.sleep(2000);
            order.setStatus(OrderStatus.DELIVERED);

            System.out.println("✅ Order " + order.getOrderId() + " completed successfully at "
                    + java.time.LocalDateTime.now());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("⚠️ Order flow interrupted for " + order.getOrderId());
        }
    }


    // ---------------- INPUT HELPERS ----------------
    private static String readNonEmptyString(Scanner scanner, String message, String fieldName) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("⚠️ " + fieldName + " cannot be empty. Please try again.");
        }
    }

    private static String readOptionalString(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static int readMenuChoice(Scanner scanner, String message, int min, int max) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("⚠️ Invalid choice. Please select between " + min + " and " + max + ".");
                } else return value;
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid input. Please enter a number.");
            }
        }
    }

    private static int readValidatedInt(Scanner scanner, String message, int min, int max, String field) {
        while (true) {
            System.out.print(message);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < min || value > max)
                    System.out.println("⚠️ Please enter a valid " + field + " between " + min + " and " + max + ".");
                else return value;
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid " + field + ". Please enter a number.");
            }
        }
    }

    private static double readValidatedDouble(Scanner scanner, String message, double min, double max, String field) {
        while (true) {
            System.out.print(message);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value < min || value > max)
                    System.out.println("⚠️ Please enter a valid " + field + " between " + min + " and " + max + ".");
                else return value;
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid " + field + ". Please enter a valid number.");
            }
        }
    }
}
