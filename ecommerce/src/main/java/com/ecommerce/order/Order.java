package com.ecommerce.order;

import com.ecommerce.order.observer.OrderObserver;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String orderId;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderObserver> observers = new ArrayList<>();

    public Order(String orderId) {
        this.orderId = orderId;
        this.status = OrderStatus.PLACED;
        this.createdAt = LocalDateTime.now();
        notifyObservers(); // initial notification
    }

    public String getOrderId() { return orderId; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void addObserver(OrderObserver observer) {
        if (observer != null) observers.add(observer);
    }

    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    public void setStatus(OrderStatus newStatus) {
        this.status = newStatus;
        notifyObservers();
    }

    private void notifyObservers() {
        for (OrderObserver obs : observers) {
            obs.update(this, status);
        }
    }
}
