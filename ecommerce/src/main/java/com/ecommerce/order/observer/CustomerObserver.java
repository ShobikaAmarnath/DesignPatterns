package com.ecommerce.order.observer;

public class CustomerObserver implements Observer {
    private String customerName;

    public CustomerObserver(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public void update(String orderId, String status) {
        System.out.println("Notification to Customer [" + customerName + "]: " +
                "Order " + orderId + " is now " + status);
    }
}
