// Author: Eason
package boundary;

import control.FrontDeskReportManager;
import control.FrontDeskServiceManager;
import entity.Booking;
import entity.LoyaltyMember;
import entity.LoyaltyTier;
import java.time.LocalDate;
import java.util.Scanner;

public class FrontDeskServiceUI {
    private final Scanner scanner;
    private final FrontDeskServiceManager manager;
    private final FrontDeskReportManager reportManager;

    public FrontDeskServiceUI(Scanner scanner, FrontDeskServiceManager manager,
            FrontDeskReportManager reportManager) {
        this.scanner = scanner;
        this.manager = manager;
        this.reportManager = reportManager;
    }

    public void displayMenu() {
        int choice;
        do {
            title("FRONT-DESK SERVICE AND LOYALTY REWARDS", 72);
            System.out.println("1. Display Bookings and Loyalty Members");
            System.out.println("2. Fast Confirmation Lookup");
            System.out.println("3. Loyalty Points and Tier Upgrade");
            System.out.println("4. Reward Redemption");
            System.out.println("5. Expired Points Cleanup");
            System.out.println("6. Reports");
            System.out.println("0. Return to Main Menu");
            line('=', 72);
            choice = readInt("Enter your choice: ", 0, 6);
            if (choice == 1) displaySubmenu();
            else if (choice == 2) lookupBooking();
            else if (choice == 3) recordSpending();
            else if (choice == 4) redeemReward();
            else if (choice == 5) cleanupExpiredPoints();
            else if (choice == 6) reportSubmenu();
            if (choice != 0) pause();
        } while (choice != 0);
    }

    private void displaySubmenu() {
        int choice;
        do {
            title("DISPLAY MENU", 72);
            System.out.println("1. Display All Bookings");
            System.out.println("2. Display All Loyalty Members");
            System.out.println("0. Return to Front-Desk Menu");
            line('=', 72);
            choice = readInt("Enter your choice: ", 0, 2);
            if (choice == 1) displayBookings(manager.getBookingsSortedByCode(), "ALL BOOKINGS");
            else if (choice == 2) displayMembers(manager.getMembersSortedById(), "ALL LOYALTY MEMBERS");
            if (choice != 0) pause();
        } while (choice != 0);
    }

    private void reportSubmenu() {
        int choice;
        do {
            title("FRONT-DESK REPORTS", 72);
            System.out.println("1. Loyalty Member Tier Distribution and Points Audit");
            System.out.println("2. High-Value Guest Analysis");
            System.out.println("0. Return to Front-Desk Menu");
            line('=', 72);
            choice = readInt("Enter your choice: ", 0, 2);
            if (choice == 1) tierDistributionReport();
            else if (choice == 2) highValueGuestReport();
            if (choice != 0) pause();
        } while (choice != 0);
    }

    private void lookupBooking() {
        title("FAST 8-DIGIT CONFIRMATION LOOKUP", 72);
        Booking booking = manager.fastLookupBooking(readRequired("Enter confirmation code: "));
        System.out.println(manager.getLastMessage());
        if (booking != null) displayBookingDetails(booking);
    }

    private void recordSpending() {
        title("LOYALTY TIER AUTO-UPGRADE ENGINE", 72);
        Booking booking = manager.fastLookupBooking(readRequired("Enter confirmation code: "));
        if (booking == null) {
            System.out.println(manager.getLastMessage());
            return;
        }
        displayBookingDetails(booking);
        LoyaltyMember member = manager.searchMemberById(booking.getMemberId());
        if (member != null) displayMemberDetails(member);
        double amount = readDouble("Additional spending amount: ", 0.01, 999999.99);
        showResult(manager.recordSpending(booking.getConfirmationCode(), amount));
        member = manager.searchMemberById(booking.getMemberId());
        if (member != null) displayMemberDetails(member);
    }

    private void redeemReward() {
        title("REWARD POINT REDEMPTION SYSTEM", 72);
        String memberId = readRequired("Enter Member ID: ");
        LoyaltyMember member = manager.searchMemberById(memberId);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }
        displayMemberDetails(member);
        line('-', 72);
        for (int i = 1; i <= 3; i++)
            System.out.printf("%d. %-24s %5d points%n", i, manager.getRewardName(i), manager.getRewardPoints(i));
        line('-', 72);
        int choice = readInt("Select reward: ", 1, 3);
        showResult(manager.redeemReward(memberId, choice));
        displayMemberDetails(member);
    }

    private void cleanupExpiredPoints() {
        title("OVERDUE / EXPIRED POINTS CLEANUP", 72);
        String dateText = readOptional("Cleanup date YYYY-MM-DD (blank for today): ");
        LocalDate date;
        try {
            date = dateText.isEmpty() ? LocalDate.now() : LocalDate.parse(dateText);
        } catch (RuntimeException exception) {
            System.out.println("Invalid date format.");
            return;
        }
        int affected = manager.cleanupExpiredPoints(date);
        System.out.println(manager.getLastMessage());
        if (affected > 0) displayMembers(manager.getMembersSortedById(), "UPDATED LOYALTY MEMBERS");
    }

    private void tierDistributionReport() {
        title("LOYALTY MEMBER TIER DISTRIBUTION AND POINTS AUDIT", 96);
        LoyaltyTier tier = readTier(true);
        LoyaltyMember[] members = reportManager.getTierDistributionReport(tier);
        displayMembers(members, "TIER DISTRIBUTION AND POINTS AUDIT");
        System.out.println("\nSummary:");
        System.out.printf("%-24s: %d%n", "Silver Members", reportManager.countTier(members, LoyaltyTier.SILVER));
        System.out.printf("%-24s: %d%n", "Gold Members", reportManager.countTier(members, LoyaltyTier.GOLD));
        System.out.printf("%-24s: %d%n", "Platinum Members", reportManager.countTier(members, LoyaltyTier.PLATINUM));
        System.out.printf("%-24s: %d%n", "Current Points", reportManager.totalPoints(members));
        System.out.printf("%-24s: %d%n", "Redeemed Points", reportManager.totalRedeemed(members));
    }

    private void highValueGuestReport() {
        title("HIGH-VALUE GUEST ANALYSIS REPORT", 96);
        int limit = readInt("Top N guests (0 for all): ", 0, manager.getMemberCount());
        LoyaltyMember[] members = reportManager.getHighValueGuestReport(limit);
        displayMembers(members, "HIGH-VALUE GUESTS SORTED BY CUSTOM MERGE SORT");
        System.out.println("\nRanking is based on total spending from highest to lowest.");
    }

    private void displayBookings(Booking[] bookings, String heading) {
        title(heading, 118);
        System.out.printf("%-10s | %-8s | %-20s | %-4s | %-12s | %-10s | %-10s | %-9s | %s%n",
                "Code", "Member", "Guest", "Room", "Room Type", "Check-In", "Check-Out", "Amount", "Status");
        line('-', 118);
        for (int i = 0; i < bookings.length; i++) System.out.println(bookings[i]);
        line('-', 118);
        System.out.println("Total Bookings: " + bookings.length);
    }

    private void displayMembers(LoyaltyMember[] members, String heading) {
        title(heading, 104);
        System.out.printf("%-8s | %-20s | %-8s | %-8s | %-7s | %-7s | %-7s | %s%n",
                "Member", "Guest", "Tier", "Spent", "Balance", "Earned", "Redeem", "Expiry");
        line('-', 104);
        for (int i = 0; i < members.length; i++) System.out.println(members[i]);
        line('-', 104);
        System.out.println("Total Members: " + members.length);
    }

    private void displayBookingDetails(Booking booking) {
        line('-', 72);
        System.out.println("Confirmation : " + booking.getConfirmationCode());
        System.out.println("Member ID    : " + booking.getMemberId());
        System.out.println("Guest Name   : " + booking.getGuestName());
        System.out.println("Room         : " + booking.getRoomNumber() + " (" + booking.getRoomType() + ")");
        System.out.println("Stay Dates   : " + booking.getCheckInDate() + " to " + booking.getCheckOutDate());
        System.out.printf("Amount       : %.2f%n", booking.getBookingAmount());
        System.out.println("Status       : " + booking.getStatus());
        line('-', 72);
    }

    private void displayMemberDetails(LoyaltyMember member) {
        line('-', 72);
        System.out.println("Member ID    : " + member.getMemberId());
        System.out.println("Guest Name   : " + member.getGuestName());
        System.out.println("Tier         : " + member.getTier().getDisplayName());
        System.out.printf("Total Spent  : %.2f%n", member.getTotalSpent());
        System.out.println("Point Balance: " + member.getPointsBalance());
        System.out.println("Earned       : " + member.getTotalPointsEarned());
        System.out.println("Redeemed     : " + member.getTotalPointsRedeemed());
        System.out.println("Expired      : " + member.getExpiredPoints());
        System.out.println("Expiry Date  : " + member.getPointsExpiryDate());
        line('-', 72);
    }

    private LoyaltyTier readTier(boolean allowAll) {
        System.out.println((allowAll ? "0=All, " : "") + "1=Silver, 2=Gold, 3=Platinum");
        int choice = readInt("Select tier: ", allowAll ? 0 : 1, 3);
        return choice == 0 ? null : LoyaltyTier.values()[choice - 1];
    }

    private int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                int number = Integer.parseInt(value);
                if (number >= minimum && number <= maximum) return number;
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Please enter a number from " + minimum + " to " + maximum + ".");
        }
    }

    private double readDouble(String prompt, double minimum, double maximum) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                double number = Double.parseDouble(value);
                if (number >= minimum && number <= maximum) return number;
            } catch (NumberFormatException ignored) {
            }
            System.out.printf("Please enter an amount from %.2f to %.2f.%n", minimum, maximum);
        }
    }

    private String readRequired(String prompt) {
        while (true) {
            String value = readOptional(prompt);
            if (!value.isEmpty()) return value;
            System.out.println("Input cannot be empty.");
        }
    }

    private String readOptional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private void showResult(boolean ignored) { System.out.println(manager.getLastMessage()); }
    private void pause() { System.out.print("Press Enter to continue..."); scanner.nextLine(); }
    private void title(String text, int width) { line('=', width); System.out.println(text); line('=', width); }
    private void line(char value, int width) {
        for (int i = 0; i < width; i++) System.out.print(value);
        System.out.println();
    }
}
