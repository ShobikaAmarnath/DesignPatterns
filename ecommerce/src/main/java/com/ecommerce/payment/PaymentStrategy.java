package com.ecommerce.payment;

// Common interface for all payment types
public interface PaymentStrategy {
    void pay(double amount);
    String name();
}
