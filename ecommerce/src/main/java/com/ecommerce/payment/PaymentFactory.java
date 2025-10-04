package com.ecommerce.payment;

public class PaymentFactory {
    // returns a strategy depending on user choice
    public static PaymentStrategy getPaymentStrategy(int option) {
        return switch (option) {
            case 1 -> new CreditCardPaymentStrategy();
            case 2 -> new PayPalPaymentStrategy();
            case 3 -> new UpiPaymentStrategy();
            default -> throw new IllegalArgumentException("Invalid payment option");
        };
    }
}
