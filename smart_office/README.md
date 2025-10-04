# Smart Office – Interactive Room Booking System

A menu-driven, production-ready Java application that simulates a smart office booking and occupancy management system. This project is built with modern software design patterns, robust exception handling, and an extensible architecture, making it an ideal showcase of clean coding principles and system design.

## Features

- **Interactive CLI Menu**: Easily configure the office, manage rooms, book or cancel reservations, and monitor real-time occupancy.

- **Dynamic Room Management**: Set the total number of rooms, update individual room capacities, and view their real-time status.

- **Smart Booking System**: Prevents booking conflicts and supports configurable booking durations to fit different meeting types.

- **Auto-Release Mechanism**: Bookings are automatically canceled if the room is not occupied within a configurable grace period, optimizing resource availability.

- **Occupancy Sensors**: Simulates people entering and leaving rooms, which automatically triggers smart devices.

- **Observer-driven IoT Simulation**: Smart devices like lights and AC systems respond automatically to changes in room occupancy.

- **Robust Logging**: Integrated with SLF4J + Logback for professional, production-grade logging.

- **Strong Validations**: Prevents invalid user input, booking conflicts, and system misuse with clear error messages.

- **Clean, Pattern-driven Architecture**: Code is structured using six key design patterns for maximum scalability and maintainability.

- **JUnit-ready**: The entire codebase is structured for easy and effective automated testing.

## Design Patterns Implemented

This project demonstrates a practical application of six different software design patterns:

**1. Behavioral Patterns**

- **Command Pattern**
    - User actions like booking, canceling, and adding occupants are encapsulated as command objects (BookRoomCommand, CancelRoomCommand, AddOccupantCommand).
    - This makes it easy to add new functionality and commands without altering the core system logic.

- **Observer Pattern**
    - Implemented for real-time occupancy management (OccupancySensor, LightSystem, ACSystem).
    - When the number of occupants in a room changes, all registered observers (the IoT devices) are automatically notified and react accordingly.

**2. Creational Patterns**

- **Singleton Pattern**
    - The OfficeConfiguration class ensures that a single, consistent configuration (e.g., number of rooms, grace period) is used throughout the application.
    - This prevents conflicts and ensures a single source of truth for system settings.

- **Factory Pattern (Lightweight Implementation)**
    - The creation of Booking objects is encapsulated, abstracting the complex instantiation logic from the client code and simplifying the booking process.

**3. Structural Patterns**

- **Facade Pattern**
    - The InteractiveShell class provides a simple, unified interface for the user to interact with the complex underlying subsystems (booking, occupancy, configuration).

- **Decorator-like Extension (via Observer)**
    - The OccupancySensor is extended with multiple "behaviors" (Lights, AC). New IoT systems (e.g., Security Cameras, Projectors) can be added as new observers without modifying any existing sensor or device code.

## Getting Started

1. **Prerequisites**
    - Java 21+
    - Apache Maven

2. **Clone the repository:**
    ```bash
    git clone https://github.com/ShobikaAmarnath/DesignPatterns.git
    cd smart_office
    ```
3. **Build the project using Maven:**
    ```bash
    mvn clean package
    ```

4. **Run the application:**
    ```bash
    java --enable-preview -jar target/smart_office.jar
    ```

5. **Usage Flow**
    
    Once the application is running, you will be greeted with an interactive menu.

    ``` 
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
    ```

## Example Demo
- **Configure the office**: e.g., 3 rooms, default capacity 10, auto-release delay of 5 minutes.

- **Book Room 1**: for 09:00, lasting 60 minutes.

- **Add occupants to Room 1**: watch as the lights and AC switch ON automatically.

- **Cancel a booking**: see the resources become available immediately.

- **Wait for auto-release**: if a booked room remains empty past the grace period, the system will automatically cancel the booking.

## Sample Inputs & Outputs

```
Enter choice: 1
Enter total number of rooms: 3
Enter default room capacity: 10
Enter auto-release delay in minutes: 1

✅ Office configured with 3 meeting rooms:
Room 1 (capacity 10)
Room 2 (capacity 10)
Room 3 (capacity 10)
```
```
Enter choice: 4
Enter room ID: 1
Enter your email: abc@gmail.com
Enter your name: abc
Enter start time (HH:mm, today): 19:15
Enter duration in minutes: 60

Room 1 booked from 19:15 for 60 minutes by abc.
```
```
Enter choice: 6
Enter room ID: 1
Enter occupant count: 10

>> 19:19:32 INFO  [main] c.s.command.AddOccupantCommand - Executing AddOccupantCommand for room 1 with count 10

>> 19:19:32 INFO  [main] c.s.observer.OccupancySensor - Sensor detected occupancy in room 1: 10 persons

>> 19:19:32 INFO  [main] c.smartoffice.manager.BookingManager - Cancelled auto-release for booking 414bad2f-5e0d-48c9-8a83-0f8afdf2f90a because room 1 is occupied

>> 19:19:32 INFO  [main] c.smartoffice.observer.LightSystem - Room 1: Lights ON (10 persons present)

>> 19:19:32 INFO  [main] com.smartoffice.observer.ACSystem - Room 1: AC ON (10 persons present)

Room 1 is now occupied by 10 persons. AC and lights turned on.
```
## Why This Project Stands Out
- **Production-Ready Practices**: Employs essential real-world techniques like logging, structured exception handling, and input validation.

- **Configurable & Flexible**: All key parameters like room count, capacity, and auto-release delay are dynamically configurable at runtime.

- **Extensible Architecture**: The Observer pattern makes it trivial to add new IoT devices (e.g., security cameras, smart projectors) with zero changes to existing code.

- **Clean Separation of Concerns**: Design patterns are applied consistently to ensure that each component has a single, well-defined responsibility.

- **Interview Advantage**: This project doesn't just deliver functionality—it demonstrates a deep, practical understanding of design patterns, clean coding, and scalable system design.

## Future Enhancements
- **Web UI**: Develop a modern web interface for a more user-friendly booking experience.

- **Database Persistence**: Integrate a database (e.g., PostgreSQL, MySQL) to persist bookings and office configurations.

- **Role-Based Access Control**: Introduce different user roles, such as Admin and User, with distinct permissions.

- **Calendar Integration**: Connect with external calendar APIs like Google Calendar or Outlook to sync bookings.'
