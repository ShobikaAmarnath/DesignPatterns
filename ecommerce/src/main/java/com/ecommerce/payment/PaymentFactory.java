package com.ecommerce.payment;

import java.util.logging.Logger;

public class PaymentFactory {
    private static final Logger logger = Logger.getLogger(PaymentFactory.class.getName());

    public static Payment createPayment(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Payment type cannot be null or empty");
        }

        return switch (type.toLowerCase()) {
            case "creditcard" -> {
                logger.info("Creating CreditCardPayment");
                yield new CreditCardPayment();
            }
            case "paypal" -> {
                logger.info("Creating PayPalPayment");
                yield new PayPalPayment();
            }
            case "upi" -> {
                logger.info("Creating UpiPayment");
                yield new UpiPayment();
            }
            default -> {
                logger.severe("Unknown payment type requested: " + type);
                throw new IllegalArgumentException("Unknown payment type: " + type);
            }
        };
    }
}
