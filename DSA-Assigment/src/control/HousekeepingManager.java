// Author: Henry
package control;

import adt.LinkedStack;
import entity.HousekeepingStaff;
import entity.HousekeepingTask;
import entity.Room;
import entity.RoomStatus;
import entity.StatusChange;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HousekeepingManager {
    private final Room[] rooms = new Room[50];
    private final HousekeepingStaff[] staffMembers = new HousekeepingStaff[20];
    private final HousekeepingTask[] tasks = new HousekeepingTask[100];
    private final LinkedStack<StatusChange> statusHistory = new LinkedStack<StatusChange>();
    private final StatusChange[] historyRecords = new StatusChange[500];
    private int roomCount;
    private int staffCount;
    private int taskCount;
    private int historyCount;
    private String lastMessage = "";
    private boolean persistenceEnabled;

    private static final String DATA_FOLDER = "data";
    private static final String ROOM_FILE = DATA_FOLDER + File.separator + "rooms.txt";
    private static final String STAFF_FILE = DATA_FOLDER + File.separator + "staff.txt";
    private static final String TASK_FILE = DATA_FOLDER + File.separator + "tasks.txt";
    private static final String HISTORY_FILE = DATA_FOLDER + File.separator + "status_history.txt";

    public void initializeData() {
        File roomFile = new File(ROOM_FILE);
        File staffFile = new File(STAFF_FILE);
        File taskFile = new File(TASK_FILE);
        if (roomFile.exists() && staffFile.exists() && taskFile.exists()) {
            loadAllData();
        }
        persistenceEnabled = true;
        saveAllData();
    }

    public boolean addRoom(Room room) {
        if (room == null || roomCount == rooms.length) return fail("Room cannot be added.");
        if (searchRoomByNumber(room.getRoomNumber()) != null) return fail("Room number already exists.");
        rooms[roomCount++] = room;
        saveIfEnabled();
        return succeed("Room added successfully.");
    }

    public boolean addStaff(HousekeepingStaff staff) {
        if (staff == null || staffCount == staffMembers.length) return fail("Staff cannot be added.");
        if (searchStaffById(staff.getStaffId()) != null) return fail("Staff ID already exists.");
        staffMembers[staffCount++] = staff;
        saveIfEnabled();
        return succeed("Staff added successfully.");
    }

    public boolean addAndAssignTask(String taskId, String roomNumber,
            String description, String staffId) {
        if (isBlank(taskId)) return fail("Task ID cannot be empty.");
        if (searchTaskById(taskId) != null) return fail("Task ID already exists.");
        Room room = searchRoomByNumber(roomNumber);
        if (room == null) return fail("Room number does not exist.");
        if (findActiveTaskForRoom(roomNumber) != null) return fail("Room already has an active task.");
        HousekeepingStaff staff = searchStaffById(staffId);
        if (staff == null) return fail("Staff ID does not exist.");
        if (!staff.isAvailable()) return fail("Selected staff member is not available.");
        if (taskCount == tasks.length) return fail("Task storage is full.");

        HousekeepingTask task = new HousekeepingTask(taskId.trim(), roomNumber,
                staff.getStaffId(), staff.getStaffName(),
                isBlank(description) ? "General housekeeping" : description.trim(),
                room.getStatus());
        tasks[taskCount++] = task;
        staff.setAvailable(false);
        if (room.getStatus() == RoomStatus.DIRTY) {
            applyStatusChange(room, RoomStatus.CLEANING_IN_PROGRESS, "Task assigned");
            task.setStatus(RoomStatus.CLEANING_IN_PROGRESS);
        }
        saveIfEnabled();
        return succeed("Task " + taskId + " has been added successfully.");
    }

    public boolean updateTaskDescription(String taskId, String newDescription) {
        HousekeepingTask task = searchTaskById(taskId);
        if (task == null) return fail("Task does not exist.");
        if (!task.isActive()) return fail("Only active tasks can be updated.");
        if (isBlank(newDescription)) return fail("Description cannot be empty.");
        task.setTaskDescription(newDescription.trim());
        saveIfEnabled();
        return succeed("Task description updated.");
    }

    public boolean changeAssignedStaff(String taskId, String newStaffId) {
        HousekeepingTask task = searchTaskById(taskId);
        if (task == null) return fail("Task does not exist.");
        if (!task.isActive()) return fail("Only active tasks can be reassigned.");
        HousekeepingStaff replacement = searchStaffById(newStaffId);
        if (replacement == null) return fail("Staff ID does not exist.");
        if (!replacement.isAvailable()) return fail("New staff member is not available.");
        HousekeepingStaff previous = searchStaffById(task.getStaffId());
        if (previous != null) previous.setAvailable(true);
        replacement.setAvailable(false);
        task.setStaffId(replacement.getStaffId());
        task.setStaffName(replacement.getStaffName());
        saveIfEnabled();
        return succeed("Assigned staff updated.");
    }

    public boolean markTaskCompleted(String taskId) {
        HousekeepingTask task = searchTaskById(taskId);
        if (task == null) return fail("Task does not exist.");
        if (!task.isActive()) return fail("Task is not active.");
        Room room = searchRoomByNumber(task.getRoomNumber());
        if (room.getStatus() != RoomStatus.READY_FOR_CHECK_IN) {
            return fail("Room must be Ready for Check-In before completing the task.");
        }
        task.setCompleted(true);
        task.setActive(false);
        task.setStatus(room.getStatus());
        HousekeepingStaff staff = searchStaffById(task.getStaffId());
        if (staff != null) staff.setAvailable(true);
        saveIfEnabled();
        return succeed("Task marked as completed.");
    }

    public boolean cancelTask(String taskId) {
        HousekeepingTask task = searchTaskById(taskId);
        if (task == null) return fail("Task does not exist.");
        if (!task.isActive()) return fail("Task is not active.");
        task.setActive(false);
        task.setCancelled(true);
        HousekeepingStaff staff = searchStaffById(task.getStaffId());
        if (staff != null) staff.setAvailable(true);
        saveIfEnabled();
        return succeed("Task cancelled; its record was retained.");
    }

    public boolean updateRoomStatus(String roomNumber, RoomStatus newStatus, String reason) {
        Room room = searchRoomByNumber(roomNumber);
        if (room == null) return fail("Room number does not exist.");
        if (!isNormalTransition(room.getStatus(), newStatus)) {
            return fail("Invalid transition from " + room.getStatus().getDisplayName()
                    + " to " + newStatus.getDisplayName() + ".");
        }
        applyStatusChange(room, newStatus, isBlank(reason) ? "Status updated" : reason.trim());
        saveIfEnabled();
        return succeed("Room status updated successfully.");
    }

    public boolean undoLatestStatusUpdate() {
        if (statusHistory.isEmpty()) return fail("No room status update is available to undo.");
        StatusChange change = statusHistory.pop();
        if (historyCount > 0) historyRecords[--historyCount] = null;
        Room room = searchRoomByNumber(change.getRoomNumber());
        if (room == null) return fail("The room in the latest history entry no longer exists.");
        room.setStatus(change.getPreviousStatus());
        synchronizeActiveTask(room);
        saveIfEnabled();
        return succeed("Restored room " + room.getRoomNumber() + " to "
                + change.getPreviousStatus().getDisplayName() + ".");
    }

    public StatusChange peekLatestStatusChange() {
        return statusHistory.isEmpty() ? null : statusHistory.peek();
    }

    public boolean recordFailedInspection(String roomNumber) {
        Room room = searchRoomByNumber(roomNumber);
        if (room == null) return fail("Room number does not exist.");
        if (room.getStatus() != RoomStatus.INSPECTED) {
            return fail("Only an inspected room can fail inspection.");
        }
        applyStatusChange(room, RoomStatus.CLEANING_IN_PROGRESS, "Failed inspection");
        saveIfEnabled();
        return succeed("Failed inspection recorded; room returned to cleaning.");
    }

    public boolean recordLateCheckout(String roomNumber) {
        Room room = searchRoomByNumber(roomNumber);
        if (room == null) return fail("Room number does not exist.");
        room.setLateCheckout(true);
        if (room.getStatus() == RoomStatus.CLEANING_IN_PROGRESS
                || room.getStatus() == RoomStatus.INSPECTED) {
            applyStatusChange(room, RoomStatus.DIRTY, "Late check-out");
        }
        saveIfEnabled();
        return succeed("Late check-out recorded.");
    }

    public Room searchRoomByNumber(String roomNumber) {
        if (roomNumber == null) return null;
        for (int i = 0; i < roomCount; i++)
            if (rooms[i].getRoomNumber().equalsIgnoreCase(roomNumber.trim())) return rooms[i];
        return null;
    }

    public HousekeepingTask searchTaskById(String taskId) {
        if (taskId == null) return null;
        for (int i = 0; i < taskCount; i++)
            if (tasks[i].getTaskId().equalsIgnoreCase(taskId.trim())) return tasks[i];
        return null;
    }

    public HousekeepingStaff searchStaffById(String staffId) {
        if (staffId == null) return null;
        for (int i = 0; i < staffCount; i++)
            if (staffMembers[i].getStaffId().equalsIgnoreCase(staffId.trim())) return staffMembers[i];
        return null;
    }

    public Room[] getRoomsSortedByNumber() {
        Room[] copy = new Room[roomCount];
        for (int i = 0; i < roomCount; i++) copy[i] = rooms[i];
        // Insertion sort inserts each item into the correct position in the sorted section.
        for (int i = 1; i < copy.length; i++) {
            Room key = copy[i]; int j = i - 1;
            while (j >= 0 && copy[j].getRoomNumber().compareTo(key.getRoomNumber()) > 0) {
                copy[j + 1] = copy[j]; j--;
            }
            copy[j + 1] = key;
        }
        return copy;
    }

    public HousekeepingStaff[] getStaffSortedById() {
        HousekeepingStaff[] copy = new HousekeepingStaff[staffCount];
        for (int i = 0; i < staffCount; i++) copy[i] = staffMembers[i];
        for (int i = 1; i < copy.length; i++) {
            HousekeepingStaff key = copy[i]; int j = i - 1;
            while (j >= 0 && copy[j].getStaffId().compareTo(key.getStaffId()) > 0) {
                copy[j + 1] = copy[j]; j--;
            }
            copy[j + 1] = key;
        }
        return copy;
    }

    public HousekeepingTask[] getTasksSortedById() {
        HousekeepingTask[] copy = new HousekeepingTask[taskCount];
        for (int i = 0; i < taskCount; i++) copy[i] = tasks[i];
        for (int i = 1; i < copy.length; i++) {
            HousekeepingTask key = copy[i]; int j = i - 1;
            while (j >= 0 && copy[j].getTaskId().compareTo(key.getTaskId()) > 0) {
                copy[j + 1] = copy[j]; j--;
            }
            copy[j + 1] = key;
        }
        return copy;
    }

    public HousekeepingStaff[] getAvailableStaff() {
        int count = 0;
        for (int i = 0; i < staffCount; i++) if (staffMembers[i].isAvailable()) count++;
        HousekeepingStaff[] result = new HousekeepingStaff[count]; int index = 0;
        for (int i = 0; i < staffCount; i++)
            if (staffMembers[i].isAvailable()) result[index++] = staffMembers[i];
        return result;
    }

    public Room[] getRoomsByStatus(RoomStatus status) {
        int count = 0;
        for (int i = 0; i < roomCount; i++) if (rooms[i].getStatus() == status) count++;
        Room[] result = new Room[count]; int index = 0;
        for (int i = 0; i < roomCount; i++)
            if (rooms[i].getStatus() == status) result[index++] = rooms[i];
        return sortRoomCopy(result);
    }

    public Room[] getLateCheckoutEligibleRooms() {
        int count = 0;
        for (int i = 0; i < roomCount; i++)
            if (rooms[i].getStatus() == RoomStatus.CLEANING_IN_PROGRESS
                    || rooms[i].getStatus() == RoomStatus.INSPECTED) count++;
        Room[] result = new Room[count]; int index = 0;
        for (int i = 0; i < roomCount; i++)
            if (rooms[i].getStatus() == RoomStatus.CLEANING_IN_PROGRESS
                    || rooms[i].getStatus() == RoomStatus.INSPECTED) result[index++] = rooms[i];
        return sortRoomCopy(result);
    }

    public Room[] getRoomsAvailableForTask() {
        int count = 0;
        for (int i = 0; i < roomCount; i++)
            if (findActiveTaskForRoom(rooms[i].getRoomNumber()) == null) count++;
        Room[] result = new Room[count]; int index = 0;
        for (int i = 0; i < roomCount; i++)
            if (findActiveTaskForRoom(rooms[i].getRoomNumber()) == null) result[index++] = rooms[i];
        return sortRoomCopy(result);
    }

    public String getLastMessage() { return lastMessage; }

    private void applyStatusChange(Room room, RoomStatus newStatus, String reason) {
        RoomStatus previous = room.getStatus();
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        StatusChange change = new StatusChange(room.getRoomNumber(), previous, newStatus, time, reason);
        statusHistory.push(change);
        if (historyCount < historyRecords.length) historyRecords[historyCount++] = change;
        room.setStatus(newStatus);
        synchronizeActiveTask(room);
    }

    private void synchronizeActiveTask(Room room) {
        HousekeepingTask task = findActiveTaskForRoom(room.getRoomNumber());
        if (task != null) task.setStatus(room.getStatus());
    }

    private HousekeepingTask findActiveTaskForRoom(String roomNumber) {
        for (int i = 0; i < taskCount; i++)
            if (tasks[i].isActive() && tasks[i].getRoomNumber().equalsIgnoreCase(roomNumber)) return tasks[i];
        return null;
    }

    private boolean isNormalTransition(RoomStatus oldStatus, RoomStatus newStatus) {
        return oldStatus == RoomStatus.DIRTY && newStatus == RoomStatus.CLEANING_IN_PROGRESS
                || oldStatus == RoomStatus.CLEANING_IN_PROGRESS && newStatus == RoomStatus.INSPECTED
                || oldStatus == RoomStatus.INSPECTED && newStatus == RoomStatus.READY_FOR_CHECK_IN;
    }

    private Room[] sortRoomCopy(Room[] source) {
        Room[] copy = new Room[source.length];
        for (int i = 0; i < source.length; i++) copy[i] = source[i];
        for (int i = 1; i < copy.length; i++) {
            Room key = copy[i]; int j = i - 1;
            while (j >= 0 && copy[j].getRoomNumber().compareTo(key.getRoomNumber()) > 0) {
                copy[j + 1] = copy[j]; j--;
            }
            copy[j + 1] = key;
        }
        return copy;
    }

    private void loadAllData() {
        roomCount = 0; staffCount = 0; taskCount = 0; historyCount = 0;
        statusHistory.clear();
        loadRooms();
        loadStaff();
        loadTasks();
        loadHistory();
    }

    private void loadRooms() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && roomCount < rooms.length) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] value = line.split("\\|", -1);
                if (value.length == 4)
                    addRoom(new Room(value[0], value[1], RoomStatus.valueOf(value[2]), Boolean.parseBoolean(value[3])));
            }
        } catch (IOException | IllegalArgumentException exception) {
            lastMessage = "Some room data could not be loaded: " + exception.getMessage();
        }
    }

    private void loadStaff() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STAFF_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && staffCount < staffMembers.length) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] value = line.split("\\|", -1);
                if (value.length == 3)
                    addStaff(new HousekeepingStaff(value[0], value[1], Boolean.parseBoolean(value[2])));
            }
        } catch (IOException exception) {
            lastMessage = "Some staff data could not be loaded: " + exception.getMessage();
        }
    }

    private void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TASK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && taskCount < tasks.length) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] value = line.split("\\|", -1);
                if (value.length == 9) {
                    HousekeepingTask task = new HousekeepingTask(value[0], value[1], value[2],
                            value[3], value[4], RoomStatus.valueOf(value[5]));
                    task.setCompleted(Boolean.parseBoolean(value[6]));
                    task.setActive(Boolean.parseBoolean(value[7]));
                    task.setCancelled(Boolean.parseBoolean(value[8]));
                    tasks[taskCount++] = task;
                }
            }
        } catch (IOException | IllegalArgumentException exception) {
            lastMessage = "Some task data could not be loaded: " + exception.getMessage();
        }
    }

    private void loadHistory() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && historyCount < historyRecords.length) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] value = line.split("\\|", -1);
                if (value.length == 5) {
                    StatusChange change = new StatusChange(value[0], RoomStatus.valueOf(value[1]),
                            RoomStatus.valueOf(value[2]), value[3], value[4]);
                    historyRecords[historyCount++] = change;
                    statusHistory.push(change);
                }
            }
        } catch (IOException | IllegalArgumentException exception) {
            lastMessage = "Some history data could not be loaded: " + exception.getMessage();
        }
    }

    public boolean saveAllData() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists() && !folder.mkdirs()) return fail("The data folder could not be created.");
        try {
            saveRooms(); saveStaff(); saveTasks(); saveHistory();
            return true;
        } catch (IOException exception) {
            lastMessage = "Data could not be saved: " + exception.getMessage();
            return false;
        }
    }

    private void saveRooms() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ROOM_FILE))) {
            writer.write("# roomNumber|roomType|status|lateCheckout"); writer.newLine();
            for (int i = 0; i < roomCount; i++) {
                Room room = rooms[i];
                writer.write(room.getRoomNumber() + "|" + clean(room.getRoomType()) + "|"
                        + room.getStatus().name() + "|" + room.isLateCheckout()); writer.newLine();
            }
        }
    }

    private void saveStaff() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STAFF_FILE))) {
            writer.write("# staffId|staffName|available"); writer.newLine();
            for (int i = 0; i < staffCount; i++) {
                HousekeepingStaff staff = staffMembers[i];
                writer.write(staff.getStaffId() + "|" + clean(staff.getStaffName()) + "|"
                        + staff.isAvailable()); writer.newLine();
            }
        }
    }

    private void saveTasks() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASK_FILE))) {
            writer.write("# taskId|roomNumber|staffId|staffName|description|status|completed|active|cancelled"); writer.newLine();
            for (int i = 0; i < taskCount; i++) {
                HousekeepingTask task = tasks[i];
                writer.write(task.getTaskId() + "|" + task.getRoomNumber() + "|" + task.getStaffId()
                        + "|" + clean(task.getStaffName()) + "|" + clean(task.getTaskDescription())
                        + "|" + task.getStatus().name() + "|" + task.isCompleted()
                        + "|" + task.isActive() + "|" + task.isCancelled()); writer.newLine();
            }
        }
    }

    private void saveHistory() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            writer.write("# roomNumber|previousStatus|newStatus|updateDateTime|reason"); writer.newLine();
            for (int i = 0; i < historyCount; i++) {
                StatusChange change = historyRecords[i];
                writer.write(change.getRoomNumber() + "|" + change.getPreviousStatus().name()
                        + "|" + change.getNewStatus().name() + "|" + change.getUpdateDateTime()
                        + "|" + clean(change.getReason())); writer.newLine();
            }
        }
    }

    private void saveIfEnabled() { if (persistenceEnabled) saveAllData(); }
    private String clean(String value) { return value == null ? "" : value.replace('|', '/').replace('\n', ' '); }

    private boolean succeed(String message) { lastMessage = message; return true; }
    private boolean fail(String message) { lastMessage = message; return false; }
    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
}
