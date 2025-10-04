package com.ecommerce.order.observer;

import com.ecommerce.order.Order;
import com.ecommerce.order.OrderStatus;

public class CustomerObserver implements OrderObserver {
    private final String customerName;

    public CustomerObserver(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public void update(Order order, OrderStatus status) {
        System.out.println("👤 [Customer: " + customerName + "] - Your order "
                + order.getOrderId() + " is now " + status + ".");
    }
}
