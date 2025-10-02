package com.smartoffice.observer;

/**
 * Observer interface for all systems that respond to occupancy changes.
 */
public interface Observer {
    void update(int roomId, int occupancyCount);
}
