package com.smartoffice.exception;

/**
 * Simple unchecked exception used for input validation failures.
 * We use unchecked so callers can choose to catch or let it bubble up to a central
 * exception handler in Main or a CLI/Invoker layer.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
