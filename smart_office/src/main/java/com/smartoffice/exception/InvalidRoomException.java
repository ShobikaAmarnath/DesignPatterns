package com.smartoffice.exception;

/**
 * Thrown when an operation references a non-existent room id.
 */
public class InvalidRoomException extends RuntimeException {
    public InvalidRoomException(String message) {
        super(message);
    }
}
