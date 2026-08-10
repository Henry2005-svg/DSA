// Author: Eason
package control;

import adt.BinarySearchTree;
import entity.Booking;
import entity.LoyaltyMember;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class FrontDeskServiceManager {
    private static final String DATA_FOLDER = "data";
    private static final String MEMBER_FILE = DATA_FOLDER + File.separator + "members.txt";
    private static final String BOOKING_FILE = DATA_FOLDER + File.separator + "bookings.txt";

    private final LoyaltyMember[] members = new LoyaltyMember[100];
    private final Booking[] bookings = new Booking[150];
    private final BinarySearchTree<String, Booking> bookingTree =
            new BinarySearchTree<String, Booking>();
    private int memberCount;
    private int bookingCount;
    private String lastMessage = "";
    private boolean persistenceEnabled;

    public void initializeData() {
        File memberFile = new File(MEMBER_FILE);
        File bookingFile = new File(BOOKING_FILE);
        if (memberFile.exists() && bookingFile.exists()) loadAllData();
        else seedData();
        persistenceEnabled = true;
        saveAllData();
    }

    public Booking fastLookupBooking(String confirmationCode) {
        if (!isValidCode(confirmationCode)) {
            lastMessage = "Confirmation code must contain exactly 8 digits.";
            return null;
        }
        Booking booking = bookingTree.search(confirmationCode.trim());
        lastMessage = booking == null ? "Booking not found." : "Booking found using Binary Search Tree lookup.";
        return booking;
    }

    public LoyaltyMember searchMemberById(String memberId) {
        if (memberId == null) return null;
        for (int i = 0; i < memberCount; i++)
            if (members[i].getMemberId().equalsIgnoreCase(memberId.trim())) return members[i];
        return null;
    }

    public boolean recordSpending(String confirmationCode, double amount) {
        Booking booking = fastLookupBooking(confirmationCode);
        if (booking == null) return false;
        LoyaltyMember member = searchMemberById(booking.getMemberId());
        if (member == null) return fail("Booking member record does not exist.");
        int earned = member.addSpending(amount);
        booking.setBookingAmount(booking.getBookingAmount() + amount);
        saveIfEnabled();
        return succeed("Spending recorded. " + earned + " points earned and tier recalculated to "
                + member.getTier().getDisplayName() + ".");
    }

    public boolean redeemReward(String memberId, int rewardChoice) {
        LoyaltyMember member = searchMemberById(memberId);
        if (member == null) return fail("Member ID does not exist.");
        int requiredPoints = getRewardPoints(rewardChoice);
        String rewardName = getRewardName(rewardChoice);
        if (requiredPoints == 0) return fail("Invalid reward selected.");
        if (!member.redeemPoints(requiredPoints))
            return fail("Insufficient points. " + rewardName + " requires " + requiredPoints + " points.");
        saveIfEnabled();
        return succeed(rewardName + " redeemed successfully. Remaining points: "
                + member.getPointsBalance() + ".");
    }

    public int cleanupExpiredPoints(LocalDate today) {
        int affected = 0;
        for (int i = 0; i < memberCount; i++) {
            LocalDate expiry = parseDate(members[i].getPointsExpiryDate());
            if (expiry != null && !expiry.isAfter(today) && members[i].getPointsBalance() > 0) {
                members[i].expirePoints();
                affected++;
            }
        }
        saveIfEnabled();
        lastMessage = affected + " member(s) had expired points cleaned up.";
        return affected;
    }

    public LoyaltyMember[] getMembersSortedById() {
        LoyaltyMember[] copy = new LoyaltyMember[memberCount];
        for (int i = 0; i < memberCount; i++) copy[i] = members[i];
        for (int i = 1; i < copy.length; i++) {
            LoyaltyMember key = copy[i];
            int j = i - 1;
            while (j >= 0 && copy[j].getMemberId().compareToIgnoreCase(key.getMemberId()) > 0) {
                copy[j + 1] = copy[j];
                j--;
            }
            copy[j + 1] = key;
        }
        return copy;
    }

    public Booking[] getBookingsSortedByCode() {
        Booking[] copy = new Booking[bookingCount];
        for (int i = 0; i < bookingCount; i++) copy[i] = bookings[i];
        for (int i = 1; i < copy.length; i++) {
            Booking key = copy[i];
            int j = i - 1;
            while (j >= 0 && copy[j].getConfirmationCode().compareTo(key.getConfirmationCode()) > 0) {
                copy[j + 1] = copy[j];
                j--;
            }
            copy[j + 1] = key;
        }
        return copy;
    }

    public int getMemberCount() { return memberCount; }
    public String getLastMessage() { return lastMessage; }

    public String getRewardName(int rewardChoice) {
        if (rewardChoice == 1) return "Room Upgrade";
        if (rewardChoice == 2) return "Free Breakfast Voucher";
        if (rewardChoice == 3) return "Late Check-Out";
        return "";
    }

    public int getRewardPoints(int rewardChoice) {
        if (rewardChoice == 1) return 3000;
        if (rewardChoice == 2) return 1200;
        if (rewardChoice == 3) return 2000;
        return 0;
    }

    private void loadAllData() {
        memberCount = 0;
        bookingCount = 0;
        bookingTree.clear();
        loadMembers();
        loadBookings();
    }

    private void loadMembers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(MEMBER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && memberCount < members.length) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] value = line.split("\\|", -1);
                if (value.length == 8) {
                    members[memberCount++] = new LoyaltyMember(value[0], value[1],
                            Double.parseDouble(value[2]), Integer.parseInt(value[3]),
                            Integer.parseInt(value[4]), Integer.parseInt(value[5]),
                            Integer.parseInt(value[6]), value[7]);
                }
            }
        } catch (IOException | NumberFormatException exception) {
            lastMessage = "Some member data could not be loaded: " + exception.getMessage();
        }
    }

    private void loadBookings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKING_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && bookingCount < bookings.length) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] value = line.split("\\|", -1);
                if (value.length == 9) addBooking(new Booking(value[0], value[1], value[2], value[3],
                        value[4], value[5], value[6], Double.parseDouble(value[7]), value[8]));
            }
        } catch (IOException | IllegalArgumentException exception) {
            lastMessage = "Some booking data could not be loaded: " + exception.getMessage();
        }
    }

    private boolean addBooking(Booking booking) {
        if (booking == null || bookingCount == bookings.length) return false;
        bookings[bookingCount++] = booking;
        bookingTree.insert(booking.getConfirmationCode(), booking);
        return true;
    }

    private void seedData() {
        memberCount = 0;
        bookingCount = 0;
        bookingTree.clear();
        members[memberCount++] = new LoyaltyMember("M1001", "Tan Jia Min", 8200.00, 5200, 9000, 3800, 0, "2026-12-31");
        members[memberCount++] = new LoyaltyMember("M1002", "Lim Wei Sheng", 15400.00, 7300, 16000, 8700, 0, "2026-09-30");
        members[memberCount++] = new LoyaltyMember("M1003", "Nur Aisyah", 28100.00, 18800, 29000, 10200, 0, "2027-01-31");
        members[memberCount++] = new LoyaltyMember("M1004", "Kavitha Rao", 4100.00, 900, 4200, 3300, 0, "2026-07-31");
        members[memberCount++] = new LoyaltyMember("M1005", "Ong Jun Kit", 33650.00, 21600, 35000, 13400, 0, "2026-11-30");
        addBooking(new Booking("12345678", "M1001", "Tan Jia Min", "101", "Deluxe", "2026-08-12", "2026-08-14", 1280.00, "Confirmed"));
        addBooking(new Booking("23456789", "M1003", "Nur Aisyah", "303", "Suite", "2026-08-13", "2026-08-16", 2400.00, "Confirmed"));
        addBooking(new Booking("34567890", "M1002", "Lim Wei Sheng", "202", "Suite", "2026-08-18", "2026-08-20", 1680.00, "Pending"));
        addBooking(new Booking("45678901", "M1005", "Ong Jun Kit", "501", "Presidential", "2026-09-01", "2026-09-04", 4500.00, "Confirmed"));
        addBooking(new Booking("56789012", "M1004", "Kavitha Rao", "206", "Standard", "2026-08-10", "2026-08-11", 320.00, "Checked-In"));
    }

    public boolean saveAllData() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists() && !folder.mkdirs()) return fail("The data folder could not be created.");
        try {
            saveMembers();
            saveBookings();
            return true;
        } catch (IOException exception) {
            return fail("Data could not be saved: " + exception.getMessage());
        }
    }

    private void saveMembers() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEMBER_FILE))) {
            writer.write("# memberId|guestName|totalSpent|pointsBalance|totalPointsEarned|totalPointsRedeemed|expiredPoints|pointsExpiryDate");
            writer.newLine();
            for (int i = 0; i < memberCount; i++) {
                LoyaltyMember member = members[i];
                writer.write(member.getMemberId() + "|" + clean(member.getGuestName()) + "|"
                        + member.getTotalSpent() + "|" + member.getPointsBalance() + "|"
                        + member.getTotalPointsEarned() + "|" + member.getTotalPointsRedeemed()
                        + "|" + member.getExpiredPoints() + "|" + member.getPointsExpiryDate());
                writer.newLine();
            }
        }
    }

    private void saveBookings() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKING_FILE))) {
            writer.write("# confirmationCode|memberId|guestName|roomNumber|roomType|checkInDate|checkOutDate|bookingAmount|status");
            writer.newLine();
            for (int i = 0; i < bookingCount; i++) {
                Booking booking = bookings[i];
                writer.write(booking.getConfirmationCode() + "|" + booking.getMemberId() + "|"
                        + clean(booking.getGuestName()) + "|" + booking.getRoomNumber() + "|"
                        + clean(booking.getRoomType()) + "|" + booking.getCheckInDate() + "|"
                        + booking.getCheckOutDate() + "|" + booking.getBookingAmount()
                        + "|" + clean(booking.getStatus()));
                writer.newLine();
            }
        }
    }

    private LocalDate parseDate(String value) {
        try { return LocalDate.parse(value); }
        catch (RuntimeException exception) { return null; }
    }

    private boolean isValidCode(String value) {
        return value != null && value.trim().matches("\\d{8}");
    }

    private void saveIfEnabled() { if (persistenceEnabled) saveAllData(); }
    private String clean(String value) { return value == null ? "" : value.replace('|', '/').replace('\n', ' '); }
    private boolean succeed(String message) { lastMessage = message; return true; }
    private boolean fail(String message) { lastMessage = message; return false; }
}
