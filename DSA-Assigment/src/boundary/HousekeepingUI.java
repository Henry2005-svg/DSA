// Author: Henry
package boundary;

import control.HousekeepingManager;
import control.HousekeepingReportManager;
import entity.HousekeepingStaff;
import entity.HousekeepingTask;
import entity.Room;
import entity.RoomStatus;
import entity.StatusChange;
import java.util.Scanner;

public class HousekeepingUI {
    private final Scanner scanner;
    private final HousekeepingManager manager;
    private final HousekeepingReportManager reportManager;

    public HousekeepingUI(Scanner scanner, HousekeepingManager manager,
            HousekeepingReportManager reportManager) {
        this.scanner = scanner;
        this.manager = manager;
        this.reportManager = reportManager;
    }

    public void displayMenu() {
        int choice;
        do {
            title("HOUSEKEEPING AND TASK LOG", 60);
            System.out.println("1. Display");
            System.out.println("2. Task Management");
            System.out.println("3. Room Status Management");
            System.out.println("4. Search");
            System.out.println("5. Reports");
            System.out.println("0. Return to Main Menu");
            line('=', 60);
            choice = readInt("Enter your choice: ", 0, 5);
            if (choice == 1) displaySubmenu();
            else if (choice == 2) taskSubmenu();
            else if (choice == 3) roomStatusSubmenu();
            else if (choice == 4) searchSubmenu();
            else if (choice == 5) reportSubmenu();
        } while (choice != 0);
    }

    private void displaySubmenu() {
        int choice;
        do {
            title("DISPLAY MENU", 60);
            System.out.println("1. Display All Rooms");
            System.out.println("2. Display All Housekeeping Staff");
            System.out.println("3. Display All Housekeeping Tasks");
            System.out.println("0. Return to Housekeeping Menu");
            line('=', 60);
            choice = readInt("Enter your choice: ", 0, 3);
            if (choice == 1) displayRooms(manager.getRoomsSortedByNumber(), "ALL ROOMS");
            else if (choice == 2) displayStaff(manager.getStaffSortedById());
            else if (choice == 3) displayTasks(manager.getTasksSortedById(), "ALL HOUSEKEEPING TASKS");
            if (choice != 0) pause();
        } while (choice != 0);
    }

    private void taskSubmenu() {
        int choice;
        do {
            title("TASK MANAGEMENT", 60);
            System.out.println("1. Add and Assign Housekeeping Task");
            System.out.println("2. Update Task Details");
            System.out.println("3. Mark Task as Completed");
            System.out.println("4. Cancel Task");
            System.out.println("0. Return to Housekeeping Menu");
            line('=', 60);
            choice = readInt("Enter your choice: ", 0, 4);
            if (choice == 1) addAndAssignTask();
            else if (choice == 2) updateTaskDetails();
            else if (choice == 3) completeTask();
            else if (choice == 4) cancelTask();
            if (choice != 0) pause();
        } while (choice != 0);
    }

    private void roomStatusSubmenu() {
        int choice;
        do {
            title("ROOM STATUS MANAGEMENT", 60);
            System.out.println("1. Update Room Status");
            System.out.println("2. Undo Latest Status Update");
            System.out.println("3. Record Failed Inspection");
            System.out.println("4. Record Late Check-Out");
            System.out.println("0. Return to Housekeeping Menu");
            line('=', 60);
            choice = readInt("Enter your choice: ", 0, 4);
            if (choice == 1) updateRoomStatus();
            else if (choice == 2) undoStatus();
            else if (choice == 3) recordFailedInspection();
            else if (choice == 4) recordLateCheckout();
            if (choice != 0) pause();
        } while (choice != 0);
    }

    private void searchSubmenu() {
        int choice;
        do {
            title("SEARCH", 60);
            System.out.println("1. Search Room by Room Number");
            System.out.println("2. Search Task by Task ID");
            System.out.println("3. Search Staff by Staff ID");
            System.out.println("0. Return to Housekeeping Menu");
            line('=', 60);
            choice = readInt("Enter your choice: ", 0, 3);
            if (choice == 1) searchRoom();
            else if (choice == 2) searchTask();
            else if (choice == 3) searchStaff();
            if (choice != 0) pause();
        } while (choice != 0);
    }

    private void reportSubmenu() {
        int choice;
        do {
            title("REPORTS", 60);
            System.out.println("1. Generate Room Status Report");
            System.out.println("2. Generate Housekeeping Task Report");
            System.out.println("0. Return to Housekeeping Menu");
            line('=', 60);
            choice = readInt("Enter your choice: ", 0, 2);
            if (choice == 1) roomReport();
            else if (choice == 2) taskReport();
            if (choice != 0) pause();
        } while (choice != 0);
    }

    private void addAndAssignTask() {
        title("ADD AND ASSIGN HOUSEKEEPING TASK", 60);
        String taskId = readRequired("Enter Task ID: ");
        displayRoomChoices(manager.getRoomsAvailableForTask(), "ROOMS WITHOUT AN ACTIVE TASK");
        String roomNumber = readRequired("Enter Room Number: ");
        Room room = manager.searchRoomByNumber(roomNumber);
        if (room == null) { System.out.println("Room not found."); return; }
        System.out.println("\nRoom Found:");
        System.out.println("Room Number : " + room.getRoomNumber());
        System.out.println("Room Type   : " + room.getRoomType());
        System.out.println("Room Status : " + room.getStatus());
        HousekeepingStaff[] available = manager.getAvailableStaff();
        if (available.length == 0) { System.out.println("No staff member is available."); return; }
        line('-', 60);
        System.out.printf("%-4s | %-8s | %-25s%n", "No.", "Staff ID", "Staff Name");
        line('-', 60);
        for (int i = 0; i < available.length; i++)
            System.out.printf("%-4d | %-8s | %-25s%n", i + 1, available[i].getStaffId(), available[i].getStaffName());
        line('-', 60);
        int selected = readInt("Select Staff: ", 1, available.length);
        String description = readRequired("Enter Task Description: ");
        if (manager.addAndAssignTask(taskId, roomNumber, description,
                available[selected - 1].getStaffId())) {
            System.out.println("Task " + taskId + " has been added successfully.");
            System.out.println("Room " + roomNumber + " has been assigned to "
                    + available[selected - 1].getStaffName() + ".");
        } else System.out.println(manager.getLastMessage());
    }

    private void updateTaskDetails() {
        String taskId = readRequired("Task ID: ");
        HousekeepingTask task = manager.searchTaskById(taskId);
        if (task == null) { System.out.println("Task not found."); return; }
        System.out.println(task);
        int choice = readInt("1. Change description  2. Change staff: ", 1, 2);
        if (choice == 1) showResult(manager.updateTaskDescription(taskId,
                readRequired("New description: ")));
        else {
            HousekeepingStaff[] available = manager.getAvailableStaff();
            for (int i = 0; i < available.length; i++)
                System.out.printf("%d. %s - %s%n", i + 1, available[i].getStaffId(), available[i].getStaffName());
            if (available.length == 0) { System.out.println("No staff member is available."); return; }
            int selected = readInt("Select new staff: ", 1, available.length);
            showResult(manager.changeAssignedStaff(taskId, available[selected - 1].getStaffId()));
        }
    }

    private void completeTask() { showResult(manager.markTaskCompleted(readRequired("Task ID: "))); }

    private void cancelTask() {
        String taskId = readRequired("Task ID: ");
        if (readYesNo("Keep the record and cancel this active task? (Y/N): "))
            showResult(manager.cancelTask(taskId));
        else System.out.println("Cancellation abandoned.");
    }

    private void updateRoomStatus() {
        displayRoomChoices(manager.getRoomsSortedByNumber(), "ROOMS AVAILABLE FOR STATUS UPDATE");
        String room = readRequired("Room number: ");
        RoomStatus status = readStatus(false);
        String reason = readRequired("Reason: ");
        showResult(manager.updateRoomStatus(room, status, reason));
    }

    private void recordFailedInspection() {
        Room[] inspectedRooms = manager.getRoomsByStatus(RoomStatus.INSPECTED);
        displayRoomChoices(inspectedRooms, "INSPECTED ROOMS ELIGIBLE TO FAIL INSPECTION");
        if (inspectedRooms.length == 0) {
            System.out.println("No inspected room is currently eligible.");
            return;
        }
        showResult(manager.recordFailedInspection(readRequired("Enter inspected room number: ")));
    }

    private void recordLateCheckout() {
        Room[] eligibleRooms = manager.getLateCheckoutEligibleRooms();
        displayRoomChoices(eligibleRooms, "ROOMS ELIGIBLE FOR LATE CHECK-OUT RESET");
        if (eligibleRooms.length == 0) {
            System.out.println("No cleaning or inspected room is currently eligible.");
            return;
        }
        showResult(manager.recordLateCheckout(readRequired("Enter room number: ")));
    }

    private void undoStatus() {
        StatusChange latest = manager.peekLatestStatusChange();
        if (latest == null) { System.out.println("No room status update is available to undo."); return; }
        System.out.println("Latest change: " + latest);
        if (readYesNo("Undo this change? (Y/N): ")) showResult(manager.undoLatestStatusUpdate());
        else System.out.println("Undo abandoned.");
    }

    private void searchRoom() {
        Room room = manager.searchRoomByNumber(readRequired("Room number: "));
        title("ROOM SEARCH RESULT", 60);
        if (room == null) { System.out.println("Room not found."); return; }
        System.out.println("Room Number : " + room.getRoomNumber());
        System.out.println("Floor       : " + room.getFloorNumber());
        System.out.println("Row         : " + room.getRowNumber());
        System.out.println("Unit        : " + room.getUnitNumber());
        System.out.println("Room Type   : " + room.getRoomType());
        System.out.println("Status      : " + room.getStatus());
        System.out.println("Late CO     : " + (room.isLateCheckout() ? "Yes" : "No"));
        line('=', 60);
    }

    private void searchTask() {
        HousekeepingTask task = manager.searchTaskById(readRequired("Task ID: "));
        title("TASK SEARCH RESULT", 60);
        if (task == null) System.out.println("Task not found.");
        else {
            System.out.println("Task ID     : " + task.getTaskId());
            System.out.println("Room        : " + task.getRoomNumber());
            System.out.println("Staff       : " + task.getStaffId() + " - " + task.getStaffName());
            System.out.println("Description : " + task.getTaskDescription());
            System.out.println("Status      : " + task.getStatus());
            System.out.println("Completed   : " + (task.isCompleted() ? "Yes" : "No"));
            System.out.println("Cancelled   : " + (task.isCancelled() ? "Yes" : "No"));
        }
    }

    private void searchStaff() {
        HousekeepingStaff staff = manager.searchStaffById(readRequired("Staff ID: "));
        title("STAFF SEARCH RESULT", 60);
        if (staff == null) System.out.println("Staff not found.");
        else {
            System.out.println("Staff ID     : " + staff.getStaffId());
            System.out.println("Staff Name   : " + staff.getStaffName());
            System.out.println("Availability : " + (staff.isAvailable() ? "Available" : "Assigned"));
        }
    }

    private void roomReport() {
        String floorText = readOptional("Floor (blank for all): ");
        Integer floor = null;
        if (!floorText.isEmpty()) {
            try { floor = Integer.valueOf(floorText); }
            catch (NumberFormatException exception) { System.out.println("Invalid floor."); return; }
        }
        RoomStatus status = readStatus(true);
        Room[] rooms = reportManager.getRoomStatusReport(floor, status);
        displayRooms(rooms, "ROOM STATUS REPORT");
        System.out.println("\nSummary:");
        System.out.printf("%-30s: %d%n", "Total Rooms", rooms.length);
        System.out.printf("%-30s: %d%n", "Dirty Rooms", reportManager.countStatus(rooms, RoomStatus.DIRTY));
        System.out.printf("%-30s: %d%n", "Cleaning In Progress Rooms", reportManager.countStatus(rooms, RoomStatus.CLEANING_IN_PROGRESS));
        System.out.printf("%-30s: %d%n", "Inspected Rooms", reportManager.countStatus(rooms, RoomStatus.INSPECTED));
        System.out.printf("%-30s: %d%n", "Ready for Check-In Rooms", reportManager.countStatus(rooms, RoomStatus.READY_FOR_CHECK_IN));
        System.out.printf("%-30s: %d%n", "Late Check-Out Rooms", reportManager.countLateCheckout(rooms));
    }

    private void taskReport() {
        String staffId = readOptional("Staff ID (blank for all): ");
        int filter = readInt("0=All, 1=Completed, 2=Incomplete: ", 0, 2);
        HousekeepingTask[] tasks = reportManager.getTaskReport(staffId, filter);
        displayTasks(tasks, "HOUSEKEEPING TASK REPORT");
        int completed = reportManager.countCompleted(tasks);
        System.out.println("\nSummary:");
        System.out.printf("%-20s: %d%n", "Total Tasks", tasks.length);
        System.out.printf("%-20s: %d%n", "Completed Tasks", completed);
        System.out.printf("%-20s: %d%n", "Incomplete Tasks", tasks.length - completed);
    }

    private void displayRooms(Room[] rooms, String heading) {
        title(heading, 76);
        System.out.printf("%-8s | %-5s | %-3s | %-4s | %-10s | %-6s | %s%n",
                "Room No.", "Floor", "Row", "Unit", "Room Type", "Status", "Late Check-Out");
        line('-', 76);
        for (int i = 0; i < rooms.length; i++) System.out.println(rooms[i]);
        line('-', 76);
        System.out.println("Total Rooms: " + rooms.length);
        System.out.println("Status Short Forms: D=Dirty, CIP=Cleaning In Progress, INS=Inspected, RFC=Ready for Check-In");
    }

    private void displayStaff(HousekeepingStaff[] staff) {
        title("ALL HOUSEKEEPING STAFF", 60);
        System.out.printf("%-8s | %-29s | %s%n", "Staff ID", "Staff Name", "Availability");
        line('-', 60);
        int available = 0;
        for (int i = 0; i < staff.length; i++) { System.out.println(staff[i]); if (staff[i].isAvailable()) available++; }
        line('-', 60);
        System.out.println("Total Staff: " + staff.length);
        System.out.println("Available Staff: " + available);
        System.out.println("Assigned Staff: " + (staff.length - available));
    }

    private void displayTasks(HousekeepingTask[] tasks, String heading) {
        title(heading, 100);
        System.out.printf("%-7s | %-4s | %-10s | %-18s | %-22s | %-6s | %s%n",
                "Task ID", "Room", "Staff ID", "Staff Name", "Description", "Status", "Completed");
        line('-', 100);
        for (int i = 0; i < tasks.length; i++) System.out.println(tasks[i]);
        line('-', 100);
        System.out.println("Total Tasks: " + tasks.length);
    }

    private void displayRoomChoices(Room[] rooms, String heading) {
        title(heading, 76);
        System.out.printf("%-8s | %-10s | %-6s | %s%n",
                "Room No.", "Room Type", "Status", "Late Check-Out");
        line('-', 76);
        for (int i = 0; i < rooms.length; i++) {
            System.out.printf("%-8s | %-10s | %-6s | %s%n",
                    rooms[i].getRoomNumber(), rooms[i].getRoomType(),
                    rooms[i].getStatus().getShortForm(),
                    rooms[i].isLateCheckout() ? "Yes" : "No");
        }
        line('-', 76);
        System.out.println("Eligible rooms: " + rooms.length);
    }

    private RoomStatus readStatus(boolean allowAll) {
        System.out.println((allowAll ? "0=All, " : "") + "1=D, 2=CIP, 3=INS, 4=RFC");
        int choice = readInt("Select status: ", allowAll ? 0 : 1, 4);
        return choice == 0 ? null : RoomStatus.values()[choice - 1];
    }

    private int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            System.out.print(prompt); String value = scanner.nextLine().trim();
            try { int number = Integer.parseInt(value); if (number >= minimum && number <= maximum) return number; }
            catch (NumberFormatException ignored) { }
            System.out.println("Please enter a number from " + minimum + " to " + maximum + ".");
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            String value = readRequired(prompt);
            if (value.equalsIgnoreCase("Y")) return true;
            if (value.equalsIgnoreCase("N")) return false;
            System.out.println("Please enter Y or N.");
        }
    }

    private String readRequired(String prompt) {
        while (true) { String value = readOptional(prompt); if (!value.isEmpty()) return value; System.out.println("Input cannot be empty."); }
    }
    private String readOptional(String prompt) { System.out.print(prompt); return scanner.nextLine().trim(); }
    private void showResult(boolean ignored) { System.out.println(manager.getLastMessage()); }
    private void pause() { System.out.print("Press Enter to continue..."); scanner.nextLine(); }
    private void title(String text, int width) { line('=', width); System.out.println(text); line('=', width); }
    private void line(char value, int width) { for (int i = 0; i < width; i++) System.out.print(value); System.out.println(); }
}
