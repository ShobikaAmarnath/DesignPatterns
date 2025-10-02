package com.smartoffice.command;

import com.smartoffice.observer.OccupancySensor;
import com.smartoffice.util.LoggerUtil;
import org.slf4j.Logger;

public class AddOccupantCommand implements Command {
    private static final Logger log = LoggerUtil.getLogger(AddOccupantCommand.class);

    private final OccupancySensor sensor;
    private final int roomId;
    private final int occupantCount;

    public AddOccupantCommand(OccupancySensor sensor, int roomId, int occupantCount) {
        this.sensor = sensor;
        this.roomId = roomId;
        this.occupantCount = occupantCount;
    }

    @Override
    public void execute() {
        log.info("Executing AddOccupantCommand for room {} with count {}", roomId, occupantCount);
        sensor.setOccupancy(roomId, occupantCount);
    }
}
