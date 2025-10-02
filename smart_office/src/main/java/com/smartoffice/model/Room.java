package com.smartoffice.model;

import com.smartoffice.exception.ValidationException;
import java.util.Objects;

/**
 * Immutable-ish Room model representing a meeting room in the office.
 * We allow updating capacity through OfficeConfiguration/Manager, not direct mutation here.
 */
public final class Room {
    private final int roomId;       // 1-based id
    private final String name;      // e.g., "Room 1"
    private final int capacity;     // maximum occupants

    public Room(int roomId, String name, int capacity) {
        if (roomId <= 0) throw new ValidationException("roomId must be positive");
        if (name == null || name.trim().isEmpty()) throw new ValidationException("name required");
        if (capacity <= 0) throw new ValidationException("capacity must be > 0");

        this.roomId = roomId;
        this.name = name.trim();
        this.capacity = capacity;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;
        return roomId == room.roomId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }

    @Override
    public String toString() {
        return STR."Room{roomId=\{roomId}, name='\{name}', capacity=\{capacity}}";
    }
}
