// Author: Henry
package boundary;

import java.util.Scanner;

public class MainMenuUI {
    private final Scanner scanner;
    private final WalkInRegistrationUI walkInUI;
    private final FrontDeskServiceUI frontDeskUI;
    private final HousekeepingUI housekeepingUI;

    public MainMenuUI(Scanner scanner, WalkInRegistrationUI walkInUI,
            FrontDeskServiceUI frontDeskUI, HousekeepingUI housekeepingUI) {
        this.scanner = scanner;
        this.walkInUI = walkInUI;
        this.frontDeskUI = frontDeskUI;
        this.housekeepingUI = housekeepingUI;
    }

    public void displayMenu() {
        int choice;
        do {
            line('=');
            System.out.println("             TAR UMT RESORT MANAGEMENT SYSTEM");
            line('=');
            System.out.println("1. Walk-In Registration and Standard Booking");
            System.out.println("2. Front-Desk Service");
            System.out.println("3. Housekeeping and Task Log");
            System.out.println("0. Exit");
            line('=');
            choice = readInt("Enter your choice: ", 0, 3);
            if (choice == 1)
                walkInUI.displayMenu();
            else if (choice == 2)
                frontDeskUI.displayMenu();
            else if (choice == 3)
                housekeepingUI.displayMenu();
        } while (choice != 0);
        System.out.println("Thank you for using TAR UMT Resort Management System.");
    }

    private int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                int number = Integer.parseInt(value);
                if (number >= minimum && number <= maximum)
                    return number;
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Please enter a number from " + minimum + " to " + maximum + ".");
        }
    }

    private void line(char value) {
        for (int i = 0; i < 60; i++)
            System.out.print(value);
        System.out.println();
    }
}
