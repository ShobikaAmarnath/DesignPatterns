package com.ecommerce.payment;

public class CreditCardPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println(String.format("Paid $%.2f using Credit Card.", amount));
    }

    @Override
    public String name() { return "Credit Card"; }
}
