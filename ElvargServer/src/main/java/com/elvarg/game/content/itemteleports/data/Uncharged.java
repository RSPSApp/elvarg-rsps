package com.elvarg.game.content.itemteleports.data;

public class Uncharged implements LastCharge {
    private int emptyId;

    public Uncharged(int emptyId) {
        this.emptyId = emptyId;
    }


    @Override
    public int emptyId() {
        return emptyId;
    }
}
