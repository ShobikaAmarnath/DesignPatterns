package com.ecommerce.order.observer;

// Observer interface
public interface Observer {
    void update(String orderId, String status);
}
