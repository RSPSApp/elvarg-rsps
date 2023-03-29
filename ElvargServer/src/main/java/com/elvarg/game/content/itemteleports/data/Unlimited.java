package com.elvarg.game.content.itemteleports.data;

import java.util.List;

public class Unlimited extends ChargeType {

    private int itemID;

    public Unlimited(int itemID) {
        this.itemID = itemID;
    }


    @Override
    public List<Integer> itemIds() {
        return List.of(itemID);
    }

    @Override
    public int getNextItem(int current) {
        return current;
    }
}
