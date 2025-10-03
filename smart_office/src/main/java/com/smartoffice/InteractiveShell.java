package com.smartoffice;

import com.smartoffice.command.AddOccupantCommand;
import com.smartoffice.command.BookRoomCommand;
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
                        showOccupancy();
                        break;
                    case 10:
                        printHelp();
                        break;
                    case 11:
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
            if (bookingManager != null) {
                System.out.println("⚠️ Office already configured. Restart the application to change configuration.");
                return;
            }

            System.out.print("Enter total number of rooms: ");
            int rooms = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter default room capacity: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter auto-release delay in minutes (e.g., 1 for testing, 5 for production): ");
            int delayMinutes = Integer.parseInt(scanner.nextLine());
            if (delayMinutes <= 0) delayMinutes = 5;

            OfficeConfiguration config = OfficeConfiguration.getInstance(rooms, capacity);
            System.out.printf("✅ Office configured with %d meeting rooms:%n", rooms);
            for (int i = 1; i <= rooms; i++) {
                System.out.printf("Room %d (capacity %d)%n", i, config.getRoomCapacity(i));
            }

            // Initialize managers & observers only now
            bookingManager = new BookingManager(Duration.ofMinutes(delayMinutes), 2);
            sensor = new OccupancySensor(bookingManager);
            sensor.registerObserver(new LightSystem());
            sensor.registerObserver(new ACSystem());
            invoker = new CommandInvoker();

        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter valid numbers for rooms, capacity, and delay.");
        } catch (IllegalStateException e) {
            System.out.printf("⚠️ Office is already configured: %s%n", e.getMessage());
        }
    }

    private void setRoomCapacity() {
        try {
            System.out.print("Enter room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            System.out.printf("Enter new capacity for Room %d: ", roomId);
            int capacity = Integer.parseInt(scanner.nextLine());

            OfficeConfiguration.getInstance().setRoomCapacity(roomId, capacity);
            System.out.printf("✅ Room %d maximum capacity set to %d.%n", roomId, capacity);

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            System.out.printf("⚠️ Error: %s%n", e.getMessage());
        }
    }

    private void showRooms() {
        OfficeConfiguration config = OfficeConfiguration.getInstance();
        Map<Integer, Integer> capacities = config.getAllRoomCapacities();
        System.out.println("\n=== Rooms & Capacities ===");
        capacities.forEach((roomId, cap) ->
                System.out.printf("Room %d → Capacity: %d%n", roomId, cap));
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

            System.out.printf("Room %d booked from %s for %d minutes by %s.%n",
                    roomId, parsedTime, duration, name);

        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid time format. Please use HH:mm (e.g., 09:30).");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter valid numbers.");
        } catch (BookingConflictException e) {
            log.info("⚠️ {}", e.getMessage());
        } catch (Exception e) {
            System.out.printf("⚠️ Error: %s%n", e.getMessage());
        }
    }

    private void cancelBooking() {
        try {
            var allBookings = bookingManager.getAllBookings();
            if (allBookings.isEmpty()) {
                System.out.println("ℹ️ No bookings exist currently.");
                return;
            }

            System.out.println("\n=== Current Bookings ===");
            allBookings.forEach((roomId, bookings) -> {
                bookings.forEach(b ->
                        System.out.printf("Room %d | Owner: %s | Start: %s | Duration: %d mins | ID: %s%n",
                                roomId, b.getOwner().getDisplayName(), b.getStart(), b.getDurationMinutes(), b.getBookingId())
                );
            });

            System.out.print("Enter booking ID (UUID) to cancel: ");
            String bookingId = scanner.nextLine().trim();

            boolean cancelled = bookingManager.cancelBooking(bookingId);
            if (cancelled) {
                System.out.printf("✅ Booking for Room cancelled successfully (ID: %s).%n", bookingId);
            } else {
                System.out.printf("⚠️ Booking not found: %s%n", bookingId);
            }

        } catch (Exception e) {
            System.out.printf("⚠️ Error: %s%n", e.getMessage());
        }
    }

    private void addOccupants() {
        try {
            System.out.print("Enter room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter occupant count: ");
            int count = Integer.parseInt(scanner.nextLine());

            invoker.executeCommand(new AddOccupantCommand(sensor, roomId, count));

            if (count == 0) {
                System.out.printf("Room %d is now unoccupied. AC and lights turned off.%n", roomId);
            } else if (count >= 2) {
                System.out.printf("Room %d is now occupied by %d persons. AC and lights turned on.%n", roomId, count);
            } else {
                System.out.printf("Room %d occupancy insufficient to mark as occupied.%n", roomId);
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            System.out.printf("⚠️ Error: %s%n", e.getMessage());
        }
    }

    private void showBookingsForRoom() {
        try {
            System.out.print("Enter room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            var bookings = bookingManager.getBookingsForRoom(roomId);
            if (bookings.isEmpty()) {
                System.out.printf("ℹ️ No bookings for Room %d%n", roomId);
            } else {
                System.out.printf("\n=== Bookings for Room %d ===%n", roomId);
                bookings.forEach(b ->
                        System.out.printf("Owner: %s | Start: %s | Duration: %d mins%n",
                                b.getOwner().getDisplayName(), b.getStart(), b.getDurationMinutes())
                );
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid room ID.");
        } catch (Exception e) {
            System.out.printf("⚠️ Error: %s%n", e.getMessage());
        }
    }

    private void showAllBookings() {
        var all = bookingManager.getAllBookings();
        boolean anyBooking = all.values().stream().anyMatch(list -> !list.isEmpty());

        System.out.println("\n=== All Bookings ===");
        if (!anyBooking) {
            System.out.println("ℹ️ No bookings currently exist.");
            return;
        }

        all.forEach((roomId, bookings) -> {
            if (!bookings.isEmpty()) {
                System.out.printf("Room %d:%n", roomId);
                bookings.forEach(b ->
                        System.out.printf("   Owner: %s | Start: %s | Duration: %d mins%n",
                                b.getOwner().getDisplayName(), b.getStart(), b.getDurationMinutes())
                );
            }
        });
    }

    private void showOccupancy() {
        try {
            System.out.print("Enter room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            int occ = bookingManager.getOccupancy(roomId);
            String status;
            if (occ == 0) status = "Room is empty.";
            else if (occ == 1) status = "Room partially occupied (1 person).";
            else status = String.format("Room occupied by %d persons.", occ);

            System.out.printf("ℹ️ Room %d occupancy: %s%n", roomId, status);

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid room ID.");
        } catch (Exception e) {
            System.out.printf("⚠️ Error: %s%n", e.getMessage());
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
