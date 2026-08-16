// Author: Henry
package boundary;

import java.util.Scanner;

public class FrontDeskServiceUI {
    private final Scanner scanner;

    public FrontDeskServiceUI(Scanner scanner) { this.scanner = scanner; }

    public void displayMenu() {
        line('=');
        System.out.println("                   FRONT-DESK SERVICE");
        line('=');
        System.out.println("This module will be implemented by another team member.");
        System.out.print("Press Enter to return to the main menu.");
        scanner.nextLine();
    }

    private void line(char value) { for (int i = 0; i < 60; i++) System.out.print(value); System.out.println(); }
}
