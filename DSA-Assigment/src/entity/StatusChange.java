// Author: Henry
package entity;

public class StatusChange {
    private final String roomNumber;
    private final RoomStatus previousStatus;
    private final RoomStatus newStatus;
    private final String updateDateTime;
    private final String reason;

    public StatusChange(String roomNumber, RoomStatus previousStatus,
            RoomStatus newStatus, String updateDateTime, String reason) {
        this.roomNumber = roomNumber;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.updateDateTime = updateDateTime;
        this.reason = reason;
    }

    public String getRoomNumber() { return roomNumber; }
    public RoomStatus getPreviousStatus() { return previousStatus; }
    public RoomStatus getNewStatus() { return newStatus; }
    public String getUpdateDateTime() { return updateDateTime; }
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return roomNumber + ": " + previousStatus.getShortForm() + " -> "
                + newStatus.getShortForm() + " at " + updateDateTime + " (" + reason + ")";
    }
}
