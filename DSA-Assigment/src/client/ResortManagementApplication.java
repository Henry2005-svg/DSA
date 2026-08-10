// Author: Henry
package client;

import boundary.FrontDeskServiceUI;
import boundary.HousekeepingUI;
import boundary.MainMenuUI;
import boundary.WalkInRegistrationUI;
import control.FrontDeskReportManager;
import control.FrontDeskServiceManager;
import control.HousekeepingManager;
import control.HousekeepingReportManager;
import java.util.Scanner;

public class ResortManagementApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HousekeepingManager manager = new HousekeepingManager();
        manager.initializeData();
        FrontDeskServiceManager frontDeskManager = new FrontDeskServiceManager();
        frontDeskManager.initializeData();
        HousekeepingReportManager reportManager = new HousekeepingReportManager(manager);
        FrontDeskReportManager frontDeskReportManager = new FrontDeskReportManager(frontDeskManager);
        HousekeepingUI housekeepingUI = new HousekeepingUI(scanner, manager, reportManager);
        MainMenuUI mainMenu = new MainMenuUI(scanner,
                new WalkInRegistrationUI(scanner),
                new FrontDeskServiceUI(scanner, frontDeskManager, frontDeskReportManager),
                housekeepingUI);
        mainMenu.displayMenu();
    }
}
