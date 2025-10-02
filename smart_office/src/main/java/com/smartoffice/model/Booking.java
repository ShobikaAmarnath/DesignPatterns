package com.smartoffice.model;

import com.smartoffice.exception.ValidationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Booking represents a reservation for a room.
 * - bookingId: unique id
 * - roomId: which room
 * - owner: User who created the booking
 * - start: start time (LocalDateTime)
 * - durationMinutes: integer > 0
 *
 * Booking is immutable after creation; cancellation or status changes are managed by BookingManager.
 */
public final class Booking {
    private final String bookingId;
    private final int roomId;
    private final User owner;
    private final LocalDateTime start;
    private final int durationMinutes;

    public Booking(int roomId, User owner, LocalDateTime start, int durationMinutes) {
        if (roomId <= 0) throw new ValidationException("roomId must be positive");
        if (owner == null) throw new ValidationException("owner required");
        if (start == null) throw new ValidationException("start time required");
        if (durationMinutes <= 0) throw new ValidationException("durationMinutes must be > 0");

        this.bookingId = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.owner = owner;
        this.start = start;
        this.durationMinutes = durationMinutes;
    }

    // Secondary constructor with explicit bookingId (useful for tests)
    Booking(String bookingId, int roomId, User owner, LocalDateTime start, int durationMinutes) {
        if (bookingId == null || bookingId.trim().isEmpty()) throw new ValidationException("bookingId required");
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.owner = owner;
        this.start = start;
        this.durationMinutes = durationMinutes;
    }

    // getters
    public String getBookingId() {
        return bookingId;
    }

    public int getRoomId() {
        return roomId;
    }

    public User getOwner() {
        return owner;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public LocalDateTime getEnd() {
        return start.plusMinutes(durationMinutes);
    }

    /**
     * Returns true if this booking overlaps with another booking (time-wise) in the same room.
     */
    public boolean overlapsWith(Booking other) {
        if (other == null) return false;
        if (this.roomId != other.roomId) return false;

        // Two intervals [s1,e1) and [s2,e2) overlap if s1 < e2 && s2 < e1
        LocalDateTime e1 = this.getEnd();
        LocalDateTime e2 = other.getEnd();

        return this.start.isBefore(e2) && other.start.isBefore(e1);
    }

    /**
     * Returns how many minutes remain until booking starts (negative if already started).
     */
    public long minutesUntilStart() {
        return Duration.between(LocalDateTime.now(), start).toMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return STR."Booking{bookingId='\{bookingId}', roomId=\{roomId}, owner=\{owner}, start=\{start}, durationMinutes=\{durationMinutes}}";
    }
}
