package com.elvarg.game.content.itemteleports.handlers;

import com.elvarg.game.content.itemteleports.data.ChargeType;
import com.elvarg.game.content.itemteleports.data.Limited;
import com.elvarg.game.content.itemteleports.data.Unlimited;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.teleportation.TeleportType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Jewellery extends ItemType {
    public List<Integer> itemIds;

    public List<Pair<String, Location>> getDestinations() {
        return destinations;
    }

    private List<Pair<String, Location>> destinations;

    private ChargeType charges;

    public ChargeType getCharges() {
        return charges;
    }


    public Jewellery(List<Pair<String, Location>> destinations, ChargeType charges) {
        this.destinations = destinations;
        this.charges = charges;
        if (charges instanceof Limited) {
            itemIds = ((Limited) charges).getItemIds();
        } else if (charges instanceof Unlimited) {
            itemIds = ((Unlimited) charges).itemIds();
        }
    }


    @Override
    public TeleportType teleportType() {
        return TeleportType.NORMAL;
    }

    @Override
    public String getMessage(Item item) {
        if(charges instanceof Limited charge) {
            return "<col=800080>There are " + charge.getRemainingCharges(item.getId()) + " charges remaining.";
        }
        return "<col=800080>There are unlimited charges remaining.";
    }
}
