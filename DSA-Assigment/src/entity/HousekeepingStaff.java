// Author: Henry
package entity;

public class HousekeepingStaff {
    private String staffId;
    private String staffName;
    private boolean available;

    public HousekeepingStaff(String staffId, String staffName, boolean available) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.available = available;
    }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("%-8s | %-29s | %s", staffId, staffName,
                available ? "Available" : "Assigned");
    }
}
