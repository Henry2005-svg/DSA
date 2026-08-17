// Author: Eason
package boundary;

import control.FrontDeskServiceManager;
import control.HousekeepingManager;
import entity.Booking;
import entity.Room;
import entity.RoomStatus;
import java.util.Scanner;

public class FrontDeskServiceUI {

    private final Scanner scanner;
    private final FrontDeskServiceManager manager;
    private final HousekeepingManager housekeepingManager;

    public FrontDeskServiceUI(
            Scanner scanner,
            FrontDeskServiceManager manager,
            HousekeepingManager housekeepingManager) {

        this.scanner = scanner;
        this.manager = manager;
        this.housekeepingManager = housekeepingManager;
    }

    public void displayMenu() {

        boolean running = true;

        while (running) {

            line('=');
            System.out.println("                 FRONT-DESK SERVICE");
            line('=');

            System.out.println("1. Search Guest by Confirmation Number");
            System.out.println("2. Check Room Availability");
            System.out.println("0. Return to Main Menu");

            line('-');

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    searchGuest();
                    break;

                case "2":
                    checkRoomAvailabilty();
                    break;

                case "0":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void searchGuest() {

        System.out.println("\nSearch Guest");

        // Ask for the unique 8-digit confirmation number
        System.out.print("Enter 8-digit confirmation number: ");
        String confirmationCode = scanner.nextLine();

        // Check whether the confirmation number is valid
        if (!manager.isValidConfirmationCode(confirmationCode)) {
            System.out.println(
                    "Invalid confirmation number. "
                    + "Please enter exactly 8 digits.");
            return;
        }

        // Search booking using the Binary Search Tree
        Booking booking =
                manager.findBookingByConfirmationCode(
                        confirmationCode);

        // Check whether the guest exists
        if (booking == null) {
            System.out.println("Guest not found.");
            return;
        }

        // Display complete guest information
        line('-');
        System.out.println("Guest Information");
        line('-');

        System.out.println(
                "Confirmation Code : "
                + booking.getConfirmationCode());

        System.out.println(
                "Guest Name        : "
                + booking.getGuestName());

        System.out.println(
                "Room Number       : "
                + booking.getRoomNumber());

        System.out.println(
                "Room Type         : "
                + booking.getRoomType());

        System.out.println(
                "Check-In Date     : "
                + booking.getCheckInDate());

        System.out.println(
                "Check-Out Date    : "
                + booking.getCheckOutDate());

        System.out.println(
                "Billing Amount    : RM"
                + booking.getBookingAmount());

        System.out.println(
                "Status            : "
                + booking.getStatus());

        line('-');
    }

    public void checkRoomAvailabilty() {

        System.out.println("\nRoom Availability");

        // Get the room data from HouseKeeping 
        Room[] rooms = housekeepingManager.getRoomsSortedByNumber();

        boolean roomAvailable = false;

        line('-');

        System.out.printf(
            "%-12s %-15s %-20s%n",
            "Room No.",
                    "Room Type",
                    "Status"
        );

        line('-');

        // Check every room 
        for (int i = 0; i < rooms.length; i++) {

            // Only show rooms that are ready for check-in
            if (rooms[i].getStatus()
                    == RoomStatus.READY_FOR_CHECK_IN) {
                
                System.out.printf(
                        "%-12s %-15s %-20s%n",
                            rooms[i].getRoomNumber(),
                            rooms[i].getRoomType(),
                            rooms[i].getStatus()
                );

                roomAvailable = true;
            }
        }

        // No available room 
        if (!roomAvailable) {
            System.out.println(
                    "No rooms are currently ready for check-in."
            );
        }

        line('-');
    }

    private void line(char value) {

        for (int i = 0; i < 60; i++) {
            System.out.print(value);
        }

        System.out.println();
    }
}