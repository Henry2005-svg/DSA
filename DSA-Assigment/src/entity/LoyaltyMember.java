// Author: Eason
package entity;

public class LoyaltyMember {
    private String memberId;
    private String guestName;
    private LoyaltyTier tier;
    private int pointsBalance;
    private int totalPointsEarned;
    private int totalPointsRedeemed;
    private int expiredPoints;
    private double totalSpent;
    private String pointsExpiryDate;

    public LoyaltyMember(String memberId, String guestName, double totalSpent,
            int pointsBalance, int totalPointsEarned, int totalPointsRedeemed,
            int expiredPoints, String pointsExpiryDate) {
        this.memberId = memberId;
        this.guestName = guestName;
        this.totalSpent = totalSpent;
        this.pointsBalance = pointsBalance;
        this.totalPointsEarned = totalPointsEarned;
        this.totalPointsRedeemed = totalPointsRedeemed;
        this.expiredPoints = expiredPoints;
        this.pointsExpiryDate = pointsExpiryDate;
        recalculateTier();
    }

    public String getMemberId() { return memberId; }
    public String getGuestName() { return guestName; }
    public LoyaltyTier getTier() { return tier; }
    public int getPointsBalance() { return pointsBalance; }
    public int getTotalPointsEarned() { return totalPointsEarned; }
    public int getTotalPointsRedeemed() { return totalPointsRedeemed; }
    public int getExpiredPoints() { return expiredPoints; }
    public double getTotalSpent() { return totalSpent; }
    public String getPointsExpiryDate() { return pointsExpiryDate; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public void setPointsExpiryDate(String pointsExpiryDate) { this.pointsExpiryDate = pointsExpiryDate; }

    public int addSpending(double amount) {
        if (amount <= 0) return 0;
        totalSpent += amount;
        int earned = (int) amount;
        pointsBalance += earned;
        totalPointsEarned += earned;
        recalculateTier();
        return earned;
    }

    public boolean redeemPoints(int points) {
        if (points <= 0 || points > pointsBalance) return false;
        pointsBalance -= points;
        totalPointsRedeemed += points;
        return true;
    }

    public int expirePoints() {
        int expired = pointsBalance;
        pointsBalance = 0;
        expiredPoints += expired;
        return expired;
    }

    public void recalculateTier() {
        tier = LoyaltyTier.calculateTier(totalSpent);
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-20s | %-8s | %8.2f | %7d | %7d | %7d | %s",
                memberId, guestName, tier.getDisplayName(), totalSpent, pointsBalance,
                totalPointsEarned, totalPointsRedeemed, pointsExpiryDate);
    }
}
