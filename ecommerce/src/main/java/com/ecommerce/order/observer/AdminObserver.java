package com.ecommerce.order.observer;

import com.ecommerce.order.Order;
import com.ecommerce.order.OrderStatus;

public class AdminObserver implements OrderObserver {
    @Override
    public void update(Order order, OrderStatus status) {
        System.out.println("🧑‍💼 [Admin Notification] - Order "
                + order.getOrderId() + " changed status to: " + status);
    }
}
