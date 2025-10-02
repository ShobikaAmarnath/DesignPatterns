package com.smartoffice.command;

import com.smartoffice.manager.BookingManager;
import com.smartoffice.model.Booking;
import com.smartoffice.util.LoggerUtil;
import org.slf4j.Logger;

public class BookRoomCommand implements Command {
    private static final Logger log = LoggerUtil.getLogger(BookRoomCommand.class);

    private final BookingManager manager;
    private final Booking booking;

    public BookRoomCommand(BookingManager manager, Booking booking) {
        this.manager = manager;
        this.booking = booking;
    }

    @Override
    public void execute() {
        log.info("Executing BookRoomCommand for booking {}", booking.getBookingId());
        manager.bookRoom(booking);
    }
}
