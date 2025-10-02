package com.smartoffice.observer;

import com.smartoffice.util.LoggerUtil;
import org.slf4j.Logger;

/**
 * ACSystem listens to occupancy updates and turns AC ON/OFF accordingly.
 */
public class ACSystem implements Observer {
    private static final Logger log = LoggerUtil.getLogger(ACSystem.class);

    @Override
    public void update(int roomId, int occupancyCount) {
        if (occupancyCount >= 2) {
            log.info("Room {}: AC ON ({} persons present)", roomId, occupancyCount);
        } else {
            log.info("Room {}: AC OFF (room empty)", roomId);
        }
    }
}
