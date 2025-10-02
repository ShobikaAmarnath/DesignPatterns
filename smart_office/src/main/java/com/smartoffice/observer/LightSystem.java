package com.smartoffice.observer;

import com.smartoffice.util.LoggerUtil;
import org.slf4j.Logger;

/**
 * LightSystem listens to occupancy updates and turns lights ON/OFF accordingly.
 */
public class LightSystem implements Observer {
    private static final Logger log = LoggerUtil.getLogger(LightSystem.class);

    @Override
    public void update(int roomId, int occupancyCount) {
        if (occupancyCount >= 2) {
            log.info("Room {}: Lights ON ({} persons present)", roomId, occupancyCount);
        } else {
            log.info("Room {}: Lights OFF (no sufficient occupancy)", roomId);
        }
    }
}
