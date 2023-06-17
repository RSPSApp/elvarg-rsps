package com.elvarg.game.content.itemteleports.data;

import java.util.List;

public class Limited extends ChargeType {

    public List<Integer> getItemIds() {
        return itemIds;
    }

    private List<Integer> itemIds;

    private LastCharge lastCharge;

    public Limited(List<Integer> itemIds, LastCharge lastCharge) {
        this.itemIds = itemIds;
        this.lastCharge = lastCharge;
    }

    @Override
    List<Integer> itemIds() {
        return itemIds;
    }

    @Override
    public int getNextItem(int current) {
        if (hasNext(current)) {
            return getNext(current);
        }
        return lastCharge.emptyId();

    }

    public int getRemainingCharges(int current) {
        if (itemIds().contains(current)) {
            return itemIds().size() - (itemIds().indexOf(current) + 1);
        }
        return 0;

    }

    public boolean hasNext(int current) {
        return itemIds().contains(current) && getRemainingCharges(current) > 0;
    }

    public int getNext(int current) {
        if (!hasNext(current)) {
            return -1;
        }

        return itemIds().get(itemIds().indexOf(current) + 1);
    }

}
