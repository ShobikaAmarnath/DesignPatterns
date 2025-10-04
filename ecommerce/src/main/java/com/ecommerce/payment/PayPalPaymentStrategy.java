package com.ecommerce.payment;

public class PayPalPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println(String.format("Paid $%.2f using PayPal.", amount));
    }

    @Override
    public String name() { return "PayPal"; }
}
