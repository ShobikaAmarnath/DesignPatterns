🏢 Smart Office – Interactive Room Booking System

A menu-driven, production-ready Java application that simulates a smart office booking and occupancy management system. Built with modern software design patterns, robust exception handling, and extensible architecture.

✨ Features

📋 Interactive CLI Menu – configure office, manage rooms, book/cancel, monitor occupancy.

🏠 Dynamic Room Management – set total rooms, update capacities, and view real-time status.

🗓️ Smart Booking System – prevents conflicts, supports configurable booking durations.

⏳ Auto-Release Mechanism – bookings auto-cancelled if not occupied within configurable grace period.

👥 Occupancy Sensors – simulate people entering/leaving, triggering automated AC & lights.

🔄 Observer-driven IoT simulation – lights and AC respond automatically to occupancy changes.

⚡ Robust Logging – SLF4J + Logback integration for professional production-grade logging.

❌ Strong Validations – prevents invalid input, conflicts, and misuse.

🧪 JUnit-ready – code structured for automated testing.

🧩 Design Patterns Implemented

This project demonstrates six different design patterns (as required):

🔹 Behavioral Patterns

Command Pattern

Used for encapsulating user actions (BookRoomCommand, CancelRoomCommand, AddOccupantCommand).

Makes it easy to extend new commands without changing core logic.

Observer Pattern

Implemented in occupancy management (OccupancySensor, LightSystem, ACSystem).

When occupants change, all observers (devices) get notified automatically.

🔹 Creational Patterns

Singleton Pattern

OfficeConfiguration ensures a single consistent configuration across the system.

Prevents multiple office re-initializations.

Factory Pattern (lightweight)

Booking creation encapsulated cleanly, hiding internal details from client code.

🔹 Structural Patterns

Facade Pattern

InteractiveShell provides a simple interface to the user, hiding complex system internals.

Decorator-like Extension (via Observer)

Occupancy sensor extended with multiple “behaviors” (Lights, AC). New systems (e.g., Security, Projector) can be added without changing existing code.

🚀 How to Run
Prerequisites

Java 21+

Maven (for dependencies)

Run the program
mvn clean package
java -jar target/smart_office.jar

📖 Usage Flow
=== Smart Office Menu ===
1. Configure office
2. Set room capacity
3. Show rooms & capacities
4. Book room
5. Cancel booking
6. Add occupants
7. Show bookings for a room
8. Show all bookings
9. Show occupancy for a room
10. Help
11. Exit


Example Demo:

Configure office → 3 rooms, capacity 10, auto-release delay 5m

Book Room 1 for 09:00, 60 mins

Add occupants (lights & AC switch ON automatically)

Cancel booking → resources freed

Auto-release triggers if room remains empty

✅ Sample Inputs & Outputs

Positive Case:

Book Room 1 09:00 60 → "Room 1 booked from 09:00 for 60 minutes."

Add occupant 1 2 → "Room 1 is now occupied by 2 persons. AC and lights turned on."

Negative Case:

Book Room 1 09:00 60 (already booked) → "Room 1 is already booked during this time."

Cancel Room 2 (not booked) → "Room 2 is not booked. Cannot cancel booking."

Add occupant 4 2 (non-existent room) → "Room 4 does not exist."

🌟 Why This Project Stands Out

✔ Production-ready practices: logging, exception handling, validation.
✔ Configurable & flexible: rooms, capacity, and auto-release delay are dynamic.
✔ Extensible architecture: easy to add new IoT devices (e.g., security cameras) via Observer.
✔ Clean separation of concerns: patterns applied consistently.
✔ User-friendly interactive shell: professional prompts, help section, clear error handling.
✔ Testing ready: structured for unit & integration tests with JUnit.
✔ Interview advantage: not just functional — demonstrates deep understanding of design patterns, clean coding, and system design.

📌 Future Enhancements

Web UI for booking instead of CLI.

Database persistence for bookings.

Role-based access (Admin vs User).

Integration with calendar APIs (Google/Outlook).

👨‍💻 Author

Developed as part of Design Patterns Coding Exercise for interview evaluation.
Focused on clarity, scalability, and demonstrating applied design patterns.