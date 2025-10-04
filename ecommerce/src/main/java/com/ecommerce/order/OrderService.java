package com.ecommerce.order;

import com.ecommerce.order.observer.OrderObserver;

public class OrderService {
    public Order placeOrder(String orderId, OrderObserver... observers) {
        Order order = new Order(orderId);
        for (OrderObserver obs : observers) {
            order.addObserver(obs);
        }

        System.out.println("🛒 Order placed successfully: " + orderId);
        return order;
    }
}
