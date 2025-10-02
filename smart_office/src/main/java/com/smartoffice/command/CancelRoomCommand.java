package com.smartoffice.command;

import com.smartoffice.manager.BookingManager;
import com.smartoffice.util.LoggerUtil;
import org.slf4j.Logger;

public class CancelRoomCommand implements Command {
    private static final Logger log = LoggerUtil.getLogger(CancelRoomCommand.class);

    private final BookingManager manager;
    private final String bookingId;

    public CancelRoomCommand(BookingManager manager, String bookingId) {
        this.manager = manager;
        this.bookingId = bookingId;
    }

    @Override
    public void execute() {
        log.info("Executing CancelRoomCommand for booking {}", bookingId);
        manager.cancelBooking(bookingId);
    }
}
