package com.smartoffice.config;

import com.smartoffice.exception.ValidationException;
import com.smartoffice.util.LoggerUtil;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Thread-safe Singleton that holds global office configuration.
 * Uses double-checked locking + volatile instance for lazy init.
 * Responsibilities:
 *  - Hold totalRooms and per-room capacities
 *  - Validate inputs (defensive programming)
 *  - Provide read-only view of configurations
 */
public final class OfficeConfiguration {
    private static final Logger log = LoggerUtil.getLogger(OfficeConfiguration.class);

    // volatile for safe-publish with double-checked locking
    private static volatile OfficeConfiguration instance;

    // configuration fields
    private int totalRooms;
    // per-room capacity map: roomId (1-based) -> capacity
    private final Map<Integer, Integer> roomCapacities;

    // private constructor
    private OfficeConfiguration(int totalRooms, int defaultCapacity) {
        if (totalRooms <= 0) {
            throw new IllegalArgumentException("totalRooms must be > 0");
        }
        if (defaultCapacity <= 0) {
            throw new IllegalArgumentException("defaultCapacity must be > 0");
        }
        this.totalRooms = totalRooms;
        this.roomCapacities = new HashMap<>(totalRooms);
        for (int i = 1; i <= totalRooms; i++) {
            this.roomCapacities.put(i, defaultCapacity);
        }
        log.info("OfficeConfiguration created: totalRooms={}, defaultCapacity={}", totalRooms, defaultCapacity);
    }

    /**
     * Obtain the singleton instance. First caller must supply totalRooms and defaultCapacity;
     * subsequent calls ignore the parameters and return the same instance.
     *
     * @param totalRooms      number of meeting rooms (required on first call)
     * @param defaultCapacity default capacity per room (required on first call)
     * @return singleton instance
     */
    public static OfficeConfiguration getInstance(int totalRooms, int defaultCapacity) {
        if (instance == null) {
            synchronized (OfficeConfiguration.class) {
                if (instance == null) {
                    instance = new OfficeConfiguration(totalRooms, defaultCapacity);
                } else {
                    LoggerUtil.getLogger(OfficeConfiguration.class).warn(
                            "OfficeConfiguration already initialized; provided parameters ignored");
                }
            }
        }
        return instance;
    }

    /**
     * Overload for subsequent calls that don't want to pass params.
     * If called before initialization, it throws IllegalStateException to force explicit init.
     */
    public static OfficeConfiguration getInstance() {
        if (instance == null) {
            throw new IllegalStateException("OfficeConfiguration not initialized. Call getInstance(totalRooms, defaultCapacity) first.");
        }
        return instance;
    }

    // getters & setters (with validations)
    public synchronized void setTotalRooms(int totalRooms) {
        if (totalRooms <= 0) throw new ValidationException("totalRooms must be > 0");
        if (totalRooms < this.totalRooms) {
            // reduce rooms — remove extra entries
            for (int i = this.totalRooms; i > totalRooms; i--) {
                roomCapacities.remove(i);
            }
        } else {
            // add new rooms with default capacity equal to nearest existing or 1
            int defaultCap = roomCapacities.getOrDefault(1, 1);
            for (int i = this.totalRooms + 1; i <= totalRooms; i++) {
                roomCapacities.put(i, defaultCap);
            }
        }
        this.totalRooms = totalRooms;
        log.info("totalRooms updated to {}", totalRooms);
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public synchronized void setRoomCapacity(int roomId, int capacity) {
        validateRoomExists(roomId);
        if (capacity <= 0) throw new ValidationException("capacity must be > 0");
        roomCapacities.put(roomId, capacity);
        log.info("Room {} capacity set to {}", roomId, capacity);
    }

    public int getRoomCapacity(int roomId) {
        validateRoomExists(roomId);
        return roomCapacities.get(roomId);
    }

    public Map<Integer, Integer> getAllRoomCapacities() {
        return Collections.unmodifiableMap(roomCapacities);
    }

    private void validateRoomExists(int roomId) {
        if (roomId <= 0 || roomId > totalRooms) {
            throw new ValidationException(STR."Invalid room id: \{roomId}");
        }
    }

    // For demonstration / testing support only: reset instance via reflection or package-private method (not exposed publicly).
    static void resetForTests() {
        instance = null;
    }

    @Override
    public String toString() {
        return STR."OfficeConfiguration{totalRooms=\{totalRooms}, roomCapacities=\{roomCapacities}}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfficeConfiguration that = (OfficeConfiguration) o;
        return totalRooms == that.totalRooms &&
                Objects.equals(roomCapacities, that.roomCapacities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalRooms, roomCapacities);
    }
}
