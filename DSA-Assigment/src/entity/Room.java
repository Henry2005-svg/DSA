// Author: Henry
package entity;

public class Room {
    private String roomNumber;
    private String roomType;
    private RoomStatus status;
    private boolean lateCheckout;

    public Room(String roomNumber, String roomType, RoomStatus status, boolean lateCheckout) {
        setRoomNumber(roomNumber);
        this.roomType = roomType;
        this.status = status;
        this.lateCheckout = lateCheckout;
    }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) {
        if (roomNumber == null || !roomNumber.matches("\\d{3}")) {
            throw new IllegalArgumentException("Room number must contain exactly three digits.");
        }
        this.roomNumber = roomNumber;
    }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public boolean isLateCheckout() { return lateCheckout; }
    public void setLateCheckout(boolean lateCheckout) { this.lateCheckout = lateCheckout; }
    public int getFloorNumber() { return roomNumber.charAt(0) - '0'; }
    public int getRowNumber() { return roomNumber.charAt(1) - '0'; }
    public int getUnitNumber() { return roomNumber.charAt(2) - '0'; }

    @Override
    public String toString() {
        return String.format("%-8s | %-5d | %-3d | %-4d | %-10s | %-6s | %s",
                roomNumber, getFloorNumber(), getRowNumber(), getUnitNumber(),
                roomType, status.getShortForm(), lateCheckout ? "Yes" : "No");
    }
}
