package com.ecommerce.order;

import com.ecommerce.order.observer.Observer;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String orderId;
    private OrderStatus status;
    private final List<Observer> observers = new ArrayList<>();

    public Order(String orderId) {
        this.orderId = orderId;
        this.status = OrderStatus.PLACED;
    }

    public void addObserver(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void setStatus(OrderStatus newStatus) {
        this.status = newStatus;
        notifyObservers();
    }

    public OrderStatus getStatus() {
        return status;
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(orderId, status.toString());
        }
    }
}
