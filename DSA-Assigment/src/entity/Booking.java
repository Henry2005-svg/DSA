package entity;

public class Booking implements Comparable<Booking> {

    private String bookingId;
    private String confirmationCode;
    private String guestName;
    private String roomNumber;
    private String roomType;
    private String checkInDate;
    private String checkOutDate;
    private double bookingAmount;
    private String status;

    public Booking(String bookingId, String confirmationCode,
            String guestName, String roomNumber, String roomType,
            String checkInDate, String checkOutDate,
            double bookingAmount, String status) {

        this.bookingId = bookingId;
        this.confirmationCode = confirmationCode;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingAmount = bookingAmount;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public double getBookingAmount() {
        return bookingAmount;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public int compareTo(Booking other) {
        // BST compares bookings using the unique 8-digit confirmation code
        return this.confirmationCode.compareTo(other.confirmationCode);
    }

    @Override
    public String toString() {
        return "Confirmation Code: " + confirmationCode
                + ", Guest Name: " + guestName
                + ", Room Number: " + roomNumber
                + ", Room Type: " + roomType
                + ", Check-In: " + checkInDate
                + ", Check-Out: " + checkOutDate
                + ", Billing Amount: RM" + bookingAmount
                + ", Status: " + status;
    }
}