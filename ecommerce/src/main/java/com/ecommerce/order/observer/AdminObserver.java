package com.ecommerce.order.observer;

public class AdminObserver implements Observer {
    @Override
    public void update(String orderId, String status) {
        System.out.println("Notification to Admin: Order " + orderId +
                " status changed to " + status);
    }
}
