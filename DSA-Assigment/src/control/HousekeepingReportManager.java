// Author: Henry
package control;

import entity.HousekeepingTask;
import entity.Room;
import entity.RoomStatus;

public class HousekeepingReportManager {
    private final HousekeepingManager manager;

    public HousekeepingReportManager(HousekeepingManager manager) {
        this.manager = manager;
    }

    public Room[] getRoomStatusReport(Integer floor, RoomStatus status) {
        Room[] source = manager.getRoomsSortedByNumber();
        int count = 0;
        for (int i = 0; i < source.length; i++)
            if (matchesRoom(source[i], floor, status)) count++;
        Room[] result = new Room[count]; int index = 0;
        for (int i = 0; i < source.length; i++)
            if (matchesRoom(source[i], floor, status)) result[index++] = source[i];
        return result;
    }

    public HousekeepingTask[] getTaskReport(String staffId, int completionFilter) {
        HousekeepingTask[] source = manager.getTasksSortedById();
        int count = 0;
        for (int i = 0; i < source.length; i++)
            if (matchesTask(source[i], staffId, completionFilter)) count++;
        HousekeepingTask[] result = new HousekeepingTask[count]; int index = 0;
        for (int i = 0; i < source.length; i++)
            if (matchesTask(source[i], staffId, completionFilter)) result[index++] = source[i];
        return result;
    }

    public int countStatus(Room[] rooms, RoomStatus status) {
        int count = 0;
        for (int i = 0; i < rooms.length; i++) if (rooms[i].getStatus() == status) count++;
        return count;
    }

    public int countLateCheckout(Room[] rooms) {
        int count = 0;
        for (int i = 0; i < rooms.length; i++) if (rooms[i].isLateCheckout()) count++;
        return count;
    }

    public int countCompleted(HousekeepingTask[] tasks) {
        int count = 0;
        for (int i = 0; i < tasks.length; i++) if (tasks[i].isCompleted()) count++;
        return count;
    }

    private boolean matchesRoom(Room room, Integer floor, RoomStatus status) {
        return (floor == null || room.getFloorNumber() == floor)
                && (status == null || room.getStatus() == status);
    }

    private boolean matchesTask(HousekeepingTask task, String staffId, int completionFilter) {
        boolean staffMatches = staffId == null || staffId.trim().isEmpty()
                || task.getStaffId().equalsIgnoreCase(staffId.trim());
        boolean completionMatches = completionFilter == 0
                || completionFilter == 1 && task.isCompleted()
                || completionFilter == 2 && !task.isCompleted();
        return staffMatches && completionMatches;
    }
}
