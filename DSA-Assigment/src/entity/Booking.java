// Author: Eason
package entity;

public class Booking {
    private String confirmationCode;
    private String memberId;
    private String guestName;
    private String roomNumber;
    private String roomType;
    private String checkInDate;
    private String checkOutDate;
    private double bookingAmount;
    private String status;

    public Booking(String confirmationCode, String memberId, String guestName,
            String roomNumber, String roomType, String checkInDate,
            String checkOutDate, double bookingAmount, String status) {
        setConfirmationCode(confirmationCode);
        this.memberId = memberId;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingAmount = bookingAmount;
        this.status = status;
    }

    public String getConfirmationCode() { return confirmationCode; }
    public String getMemberId() { return memberId; }
    public String getGuestName() { return guestName; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public String getCheckInDate() { return checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public double getBookingAmount() { return bookingAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setBookingAmount(double bookingAmount) { this.bookingAmount = bookingAmount; }

    public void setConfirmationCode(String confirmationCode) {
        if (confirmationCode == null || !confirmationCode.matches("\\d{8}"))
            throw new IllegalArgumentException("Confirmation code must contain exactly 8 digits.");
        this.confirmationCode = confirmationCode;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-8s | %-20s | %-4s | %-12s | %-10s | %-10s | %9.2f | %s",
                confirmationCode, memberId, guestName, roomNumber, roomType,
                checkInDate, checkOutDate, bookingAmount, status);
    }
}
