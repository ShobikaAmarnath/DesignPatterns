package com.smartoffice;

import com.smartoffice.command.*;
import com.smartoffice.config.OfficeConfiguration;
import com.smartoffice.manager.BookingManager;
import com.smartoffice.model.Booking;
import com.smartoffice.model.User;
import com.smartoffice.observer.ACSystem;
import com.smartoffice.observer.LightSystem;
import com.smartoffice.observer.OccupancySensor;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Smart Office Demo Started ===");

        // 1. Singleton: Configure office (3 rooms, default capacity 10)
        OfficeConfiguration config = OfficeConfiguration.getInstance(3, 10);
        System.out.println(STR."Office configured with \{config.getTotalRooms()} meeting rooms.");

        // 2. BookingManager with auto-release = 20 seconds (short for demo; real = 5 mins)
        BookingManager bookingManager = new BookingManager(Duration.ofSeconds(20), 2);

        // 3. OccupancySensor (Subject) + Observers (AC + Light)
        OccupancySensor sensor = new OccupancySensor(bookingManager);
        sensor.registerObserver(new LightSystem());
        sensor.registerObserver(new ACSystem());

        // 4. CommandInvoker
        CommandInvoker invoker = new CommandInvoker();

        // --- Demo flow ---

        // Book a room
        User bob = new User("bob@example.com", "Bob");
        Booking booking = new Booking(1, bob, LocalDateTime.now().plusSeconds(10), 60);
        invoker.executeCommand(new BookRoomCommand(bookingManager, booking));
        System.out.println("Room 1 booked for Bob.");

        // Simulate occupancy
        invoker.executeCommand(new AddOccupantCommand(sensor, 1, 0)); // empty
        Thread.sleep(2000); // wait 2 sec
        invoker.executeCommand(new AddOccupantCommand(sensor, 1, 2)); // occupied
        Thread.sleep(2000); // wait 2 sec
        invoker.executeCommand(new AddOccupantCommand(sensor, 1, 0)); // empty again

        // Cancel booking manually
        invoker.executeCommand(new CancelRoomCommand(bookingManager, booking.getBookingId()));

        // Optional: wait to see auto-release in logs if booking not occupied
        System.out.println("Waiting 25 sec to observe auto-release if any bookings remain...");
        Thread.sleep(25000);

        // Shutdown
        bookingManager.shutdownNow();
        System.out.println("=== Smart Office Demo Finished ===");
    }
}
