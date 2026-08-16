// Author: Eason
package entity;

public enum LoyaltyTier {
    SILVER("Silver", 0),
    GOLD("Gold", 10000),
    PLATINUM("Platinum", 25000);

    private final String displayName;
    private final double minimumSpend;

    LoyaltyTier(String displayName, double minimumSpend) {
        this.displayName = displayName;
        this.minimumSpend = minimumSpend;
    }

    public String getDisplayName() { return displayName; }
    public double getMinimumSpend() { return minimumSpend; }

    public static LoyaltyTier calculateTier(double totalSpent) {
        if (totalSpent >= PLATINUM.minimumSpend) return PLATINUM;
        if (totalSpent >= GOLD.minimumSpend) return GOLD;
        return SILVER;
    }
}
