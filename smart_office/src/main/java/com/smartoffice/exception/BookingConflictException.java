package com.smartoffice.exception;

/**
 * Thrown when a new booking conflicts with existing bookings for the same room.
 */
public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}
