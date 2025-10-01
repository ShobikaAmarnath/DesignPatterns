package com.ecommerce.order;

import com.ecommerce.order.observer.Observer;
import java.util.logging.Logger;

public class OrderService {
    private static final Logger logger = Logger.getLogger(OrderService.class.getName());

    public Order placeOrder(String orderId, Observer... observers) {
        Order order = new Order(orderId);
        for (Observer observer : observers) {
            order.addObserver(observer);
        }
        logger.info("Order placed successfully: " + orderId);
        return order;
    }
}
