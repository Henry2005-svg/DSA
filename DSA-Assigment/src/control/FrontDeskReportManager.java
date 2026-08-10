// Author: Eason
package control;

import entity.LoyaltyMember;
import entity.LoyaltyTier;

public class FrontDeskReportManager {
    private final FrontDeskServiceManager manager;

    public FrontDeskReportManager(FrontDeskServiceManager manager) {
        this.manager = manager;
    }

    public LoyaltyMember[] getTierDistributionReport(LoyaltyTier tier) {
        LoyaltyMember[] source = manager.getMembersSortedById();
        int count = 0;
        for (int i = 0; i < source.length; i++)
            if (tier == null || source[i].getTier() == tier) count++;
        LoyaltyMember[] result = new LoyaltyMember[count];
        int index = 0;
        for (int i = 0; i < source.length; i++)
            if (tier == null || source[i].getTier() == tier) result[index++] = source[i];
        return result;
    }

    public LoyaltyMember[] getHighValueGuestReport(int limit) {
        LoyaltyMember[] source = manager.getMembersSortedById();
        mergeSortBySpending(source, 0, source.length - 1);
        int resultSize = limit <= 0 || limit > source.length ? source.length : limit;
        LoyaltyMember[] result = new LoyaltyMember[resultSize];
        for (int i = 0; i < resultSize; i++) result[i] = source[i];
        return result;
    }

    public int countTier(LoyaltyMember[] members, LoyaltyTier tier) {
        int count = 0;
        for (int i = 0; i < members.length; i++) if (members[i].getTier() == tier) count++;
        return count;
    }

    public int totalPoints(LoyaltyMember[] members) {
        int total = 0;
        for (int i = 0; i < members.length; i++) total += members[i].getPointsBalance();
        return total;
    }

    public int totalRedeemed(LoyaltyMember[] members) {
        int total = 0;
        for (int i = 0; i < members.length; i++) total += members[i].getTotalPointsRedeemed();
        return total;
    }

    private void mergeSortBySpending(LoyaltyMember[] data, int left, int right) {
        if (left >= right) return;
        int middle = (left + right) / 2;
        mergeSortBySpending(data, left, middle);
        mergeSortBySpending(data, middle + 1, right);
        merge(data, left, middle, right);
    }

    private void merge(LoyaltyMember[] data, int left, int middle, int right) {
        LoyaltyMember[] temp = new LoyaltyMember[right - left + 1];
        int i = left;
        int j = middle + 1;
        int k = 0;
        while (i <= middle && j <= right) {
            if (data[i].getTotalSpent() >= data[j].getTotalSpent()) temp[k++] = data[i++];
            else temp[k++] = data[j++];
        }
        while (i <= middle) temp[k++] = data[i++];
        while (j <= right) temp[k++] = data[j++];
        for (int index = 0; index < temp.length; index++) data[left + index] = temp[index];
    }
}
