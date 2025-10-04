package com.ecommerce.payment;

public class UpiPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println(String.format("Paid $%.2f using UPI.", amount));
    }

    @Override
    public String name() { return "UPI"; }
}
