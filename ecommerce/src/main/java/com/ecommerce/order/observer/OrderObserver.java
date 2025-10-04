package com.ecommerce.order.observer;

import com.ecommerce.order.Order;
import com.ecommerce.order.OrderStatus;

public interface OrderObserver {
    void update(Order order, OrderStatus status);
}
