package com.smartoffice.observer;

import com.smartoffice.manager.BookingManager;
import com.smartoffice.util.LoggerUtil;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * OccupancySensor acts as the Subject in Observer pattern.
 * - Keeps track of registered Observers (AC, LightSystem, etc.)
 * - Notifies them whenever occupancy changes
 * - Also informs BookingManager (so bookings can be auto-released if empty)
 */
public class OccupancySensor {
    private static final Logger log = LoggerUtil.getLogger(OccupancySensor.class);

    private final List<Observer> observers = new ArrayList<>();
    private final BookingManager bookingManager;

    public OccupancySensor(BookingManager bookingManager) {
        this.bookingManager = bookingManager;
    }

    public void registerObserver(Observer obs) {
        observers.add(obs);
    }

    public void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    public void setOccupancy(int roomId, int count) {
        log.info("Sensor detected occupancy in room {}: {} persons", roomId, count);

        // Notify booking manager (affects auto-release logic)
        bookingManager.updateOccupancy(roomId, count);

        // Notify all observers (lights, AC, etc.)
        for (Observer obs : observers) {
            obs.update(roomId, count);
        }
    }
}
