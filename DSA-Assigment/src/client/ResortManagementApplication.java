// Author: Henry
package client;

import boundary.FrontDeskServiceUI;
import boundary.HousekeepingUI;
import boundary.MainMenuUI;
import boundary.WalkInRegistrationUI;
import control.HousekeepingManager;
import control.HousekeepingReportManager;
import java.util.Scanner;

public class ResortManagementApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HousekeepingManager manager = new HousekeepingManager();
        manager.initializeData();
        HousekeepingReportManager reportManager = new HousekeepingReportManager(manager);
        HousekeepingUI housekeepingUI = new HousekeepingUI(scanner, manager, reportManager);
        MainMenuUI mainMenu = new MainMenuUI(scanner,
                new WalkInRegistrationUI(scanner),
                new FrontDeskServiceUI(scanner), housekeepingUI);
        mainMenu.displayMenu();
    }
}
