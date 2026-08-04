// Author: Henry
package entity;

public class HousekeepingTask {
    private String taskId;
    private String roomNumber;
    private String staffId;
    private String staffName;
    private String taskDescription;
    private RoomStatus status;
    private boolean completed;
    private boolean active;
    private boolean cancelled;

    public HousekeepingTask(String taskId, String roomNumber, String staffId,
            String staffName, String taskDescription, RoomStatus status) {
        this.taskId = taskId;
        this.roomNumber = roomNumber;
        this.staffId = staffId;
        this.staffName = staffName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.completed = false;
        this.active = true;
        this.cancelled = false;
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String value) { taskDescription = value; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public String toString() {
        return String.format("%-7s | %-4s | %-10s | %-18s | %-22s | %-6s | %s",
                taskId, roomNumber, staffId, staffName, taskDescription,
                status.getShortForm(), completed ? "Yes" : "No");
    }
}
