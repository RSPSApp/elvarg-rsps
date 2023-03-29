package com.elvarg.game.content.itemteleports.data;

import java.util.List;

public abstract class ChargeType {
    abstract List<Integer> itemIds();

    public abstract int getNextItem(int current);

}
