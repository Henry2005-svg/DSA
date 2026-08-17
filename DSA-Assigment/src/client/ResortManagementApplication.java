// Author: Henry
package client;

import boundary.FrontDeskServiceUI;
import boundary.HousekeepingUI;
import boundary.MainMenuUI;
import boundary.WalkInRegistrationUI;
import control.FrontDeskServiceManager;
import control.HousekeepingManager;
import control.HousekeepingReportManager;
import java.util.Scanner;

public class ResortManagementApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        HousekeepingManager manager = 
                new HousekeepingManager();

        // Create the Front Desk manager
        FrontDeskServiceManager frontDeskManager = 
                new FrontDeskServiceManager();

        // Load booking data into the Binary Search Tree2
        frontDeskManager.loadBookingsFromFile(
                "DSA-Assigment/data/bookings.txt");

        manager.initializeData();

        HousekeepingReportManager reportManager = 
                new HousekeepingReportManager(manager);

        HousekeepingUI housekeepingUI = 
                new HousekeepingUI(
                        scanner, 
                        manager, 
                        reportManager);

        MainMenuUI mainMenu = 
                new MainMenuUI(
                    scanner,
                    new WalkInRegistrationUI(scanner),
                    new FrontDeskServiceUI(
                            scanner, 
                            frontDeskManager), 
                        housekeepingUI);
                        
        mainMenu.displayMenu();
    }
}
