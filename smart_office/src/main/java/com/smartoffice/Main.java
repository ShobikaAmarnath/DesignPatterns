package com.smartoffice;


import com.smartoffice.command.*;
import com.smartoffice.config.OfficeConfiguration;
import com.smartoffice.manager.BookingManager;
import com.smartoffice.manager.OccupancyManager;
import com.smartoffice.observer.ACSystem;
import com.smartoffice.observer.LightSystem;
import com.smartoffice.observer.OccupancySensor;
import com.smartoffice.exception.*;


import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class Main {
public static void main(String[] args) {
LoggerUtil.init();
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);


OfficeConfiguration config = OfficeConfiguration.getInstance();
BookingManager bookingManager = new BookingManager(scheduler);
OccupancyManager occupancyManager = new OccupancyManager(scheduler);


// attach global observers to all rooms via sensor-managed subscriptions
occupancyManager.registerObserver(new LightSystem());
occupancyManager.registerObserver(new ACSystem());


Scanner sc = new Scanner(System.in);
System.out.println("Welcome to Smart Office Console. Type 'help' for commands.");


CommandParser parser = new CommandParser(bookingManager, occupancyManager, config);


boolean running = true;
while (running) {
System.out.print("> ");
String line = sc.nextLine().trim();
if (line.isEmpty()) continue;
if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
running = false;
continue;
}
try {
parser.parseAndExecute(line);
} catch (Exception e) {
LoggerUtil.getLogger().severe("Error handling command: " + e.getMessage());
System.out.println("Error: " + e.getMessage());
}
}


scheduler.shutdownNow();
sc.close();
System.out.println("Goodbye.");
}
}