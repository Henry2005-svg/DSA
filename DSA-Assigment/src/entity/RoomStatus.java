// Author: Henry
package entity;

public enum RoomStatus {
    DIRTY("D", "Dirty"),
    CLEANING_IN_PROGRESS("CIP", "Cleaning In Progress"),
    INSPECTED("INS", "Inspected"),
    READY_FOR_CHECK_IN("RFC", "Ready for Check-In");

    private final String shortForm;
    private final String displayName;

    RoomStatus(String shortForm, String displayName) {
        this.shortForm = shortForm;
        this.displayName = displayName;
    }

    public String getShortForm() { return shortForm; }
    public String getDisplayName() { return displayName; }

    @Override
    public String toString() { return shortForm + " - " + displayName; }
}
