package com.smartoffice;

import com.smartoffice.command.AddOccupantCommand;
import com.smartoffice.command.BookRoomCommand;
import com.smartoffice.command.CancelRoomCommand;
import com.smartoffice.command.CommandInvoker;
import com.smartoffice.config.OfficeConfiguration;
import com.smartoffice.exception.BookingConflictException;
import com.smartoffice.manager.BookingManager;
import com.smartoffice.model.Booking;
import com.smartoffice.model.User;
import com.smartoffice.observer.ACSystem;
import com.smartoffice.observer.LightSystem;
import com.smartoffice.observer.OccupancySensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;

public class InteractiveShell {

    private static final Logger log = LoggerFactory.getLogger(InteractiveShell.class);
    private final Scanner scanner = new Scanner(System.in);

    // Lazy initialized after office config
    private BookingManager bookingManager;
    private OccupancySensor sensor;
    private CommandInvoker invoker;

    public void run() {
        boolean exitRequested = false;

        do {
            printMenu();
            String choiceStr = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException ex) {
                System.out.println("❌ Invalid choice. Please enter a number.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        configureOffice();
                        break;
                    case 2:
                        requireConfig();
                        setRoomCapacity();
                        break;
                    case 3:
                        requireConfig();
                        showRooms();
                        break;
                    case 4:
                        requireConfig();
                        bookRoom();
                        break;
                    case 5:
                        requireConfig();
                        cancelBooking();
                        break;
                    case 6:
                        requireConfig();
                        addOccupants();
                        break;
                    case 7:
                        requireConfig();
                        showBookingsForRoom();
                        break;
                    case 8:
                        requireConfig();
                        showAllBookings();
                        break;
                    case 9:
                        requireConfig();
                        System.out.println("👉 Show occupancy - (to be implemented)");
                        break;
                    case 10:
                        printHelp();
                        break;
                    case 0:
                        exitRequested = true;
                        System.out.println("👋 Exiting Smart Office. Goodbye!");
                        if (bookingManager != null) {
                            bookingManager.shutdownNow();
                        }
                        break;
                    default:
                        System.out.println("❌ Invalid choice. Please try again.");
                }
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        } while (!exitRequested);
    }

    private void configureOffice() {
        try {
            System.out.print("Enter total number of rooms: ");
            int rooms = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter default room capacity: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            OfficeConfiguration config = OfficeConfiguration.getInstance(rooms, capacity);
            System.out.println("✅ Office configured with " + rooms + " rooms, default capacity " + capacity);

            // Initialize managers & observers only now
            bookingManager = new BookingManager(Duration.ofMinutes(5), 2); // 5 min auto-release, 2 person threshold
            sensor = new OccupancySensor(bookingManager);
            sensor.registerObserver(new LightSystem());
            sensor.registerObserver(new ACSystem());
            invoker = new CommandInvoker();

        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter valid numbers for rooms and capacity.");
        } catch (IllegalStateException e) {
            System.out.println("⚠️ Office is already configured: " + e.getMessage());
        }
    }

    private void setRoomCapacity() {
        try {
            System.out.print("Enter room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            System.out.print(STR."Enter new capacity for Room \{roomId}: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            OfficeConfiguration.getInstance().setRoomCapacity(roomId, capacity);
            System.out.println(STR."✅ Room \{roomId} capacity updated to \{capacity}");

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            log.info(STR."⚠ Error: \{e.getMessage()}");
        }
    }

    private void showRooms() {
        OfficeConfiguration config = OfficeConfiguration.getInstance();
        Map<Integer, Integer> capacities = config.getAllRoomCapacities();
        System.out.println("\n=== Rooms & Capacities ===");
        capacities.forEach((roomId, cap) ->
                System.out.println("Room " + roomId + " → Capacity: " + cap));
    }

    private void bookRoom() {
        try {
            System.out.print("Enter room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter your email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter start time (HH:mm, today): ");
            String timeStr = scanner.nextLine().trim();

            System.out.print("Enter duration in minutes: ");
            int duration = Integer.parseInt(scanner.nextLine());

            // Parse start time as today + HH:mm
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime parsedTime = LocalTime.parse(timeStr, fmt);
            LocalDateTime start = LocalDateTime.of(now.toLocalDate(), parsedTime);

            User user = new User(email, name);
            Booking booking = new Booking(roomId, user, start, duration);

            invoker.executeCommand(new BookRoomCommand(bookingManager, booking));
            System.out.println("✅ Booking created successfully. ID: " + booking.getBookingId());

        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid time format. Please use HH:mm (e.g., 09:30).");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter valid numbers.");
        } catch (BookingConflictException e) {
            System.out.println("⚠️ Booking conflict: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

    private void cancelBooking() {
        try {
            System.out.print("Enter booking ID (UUID): ");
            String bookingId = scanner.nextLine().trim();

            invoker.executeCommand(new CancelRoomCommand(bookingManager, bookingId));
            System.out.println("✅ Booking " + bookingId + " cancelled.");

        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

    private void addOccupants() {
        try {
            System.out.print("Enter room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter occupant count: ");
            int count = Integer.parseInt(scanner.nextLine());

            invoker.executeCommand(new AddOccupantCommand(sensor, roomId, count));
            System.out.println("✅ Occupancy updated for Room " + roomId + " → " + count + " persons");

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

    private void showBookingsForRoom() {
        try {
            System.out.print("Enter room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            var bookings = bookingManager.getBookingsForRoom(roomId);
            if (bookings.isEmpty()) {
                System.out.println("ℹ️ No bookings for Room " + roomId);
            } else {
                System.out.println("\n=== Bookings for Room " + roomId + " ===");
                bookings.forEach(b ->
                        System.out.println("ID: " + b.getBookingId() +
                                " | Owner: " + b.getOwner().getDisplayName() +
                                " | Start: " + b.getStart() +
                                " | Duration: " + b.getDurationMinutes() + " mins")
                );
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid room ID.");
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

    private void showAllBookings() {
        var all = bookingManager.getAllBookings();
        if (all.isEmpty()) {
            System.out.println("ℹ️ No bookings currently exist.");
        } else {
            System.out.println("\n=== All Bookings ===");
            all.forEach((roomId, bookings) -> {
                System.out.println("Room " + roomId + ":");
                bookings.forEach(b ->
                        System.out.println("   ID: " + b.getBookingId() +
                                " | Owner: " + b.getOwner().getDisplayName() +
                                " | Start: " + b.getStart() +
                                " | Duration: " + b.getDurationMinutes() + " mins")
                );
            });
        }
    }

    private void requireConfig() {
        if (bookingManager == null) {
            throw new IllegalStateException("❌ Please configure the office first (option 1).");
        }
    }

    private void printMenu() {
        System.out.println("\n=== Smart Office Menu ===");
        System.out.println("1. Configure office");
        System.out.println("2. Set room capacity");
        System.out.println("3. Show rooms & capacities");
        System.out.println("4. Book room");
        System.out.println("5. Cancel booking");
        System.out.println("6. Add occupants");
        System.out.println("7. Show bookings for a room");
        System.out.println("8. Show all bookings");
        System.out.println("9. Show occupancy for a room");
        System.out.println("10. Help");
        System.out.println("11. Exit");
        System.out.print("Enter choice: ");
    }

    private void printHelp() {
        System.out.println("\n=== Help ===");
        System.out.println("This is a menu-driven Smart Office application.");
        System.out.println("Start with option 1 (configure office). Then use booking, occupancy, and viewing features.");
    }
}
