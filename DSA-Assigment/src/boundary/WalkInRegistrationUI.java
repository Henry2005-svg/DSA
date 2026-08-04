// Author: Henry
package boundary;

import java.util.Scanner;

public class WalkInRegistrationUI {
    private final Scanner scanner;

    public WalkInRegistrationUI(Scanner scanner) { this.scanner = scanner; }

    public void displayMenu() {
        line('=');
        System.out.println("        WALK-IN REGISTRATION AND STANDARD BOOKING");
        line('=');
        System.out.println("This module will be implemented by another team member.");
        System.out.print("Press Enter to return to the main menu.");
        scanner.nextLine();
    }

    private void line(char value) { for (int i = 0; i < 60; i++) System.out.print(value); System.out.println(); }
}
