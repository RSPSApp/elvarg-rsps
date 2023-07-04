package com.elvarg.game.definition.loader.impl;

import com.elvarg.util.Misc;

import java.text.DecimalFormat;

/**
 * @author Ynneh | 04/07/2023 - 13:28
 * <https://github.com/drhenny>
 */
public class OSBoxNPCDrops {

    public int id;
    public String name;
    public boolean members;
    public String quantity;
    public boolean noted;
    public double rarity;
    public int rolls;

    public int minAmount;
    public int maxAmount;

    /** Converted from string format to integer **/
    public int getQuantity() {
        if (minAmount > 0 && maxAmount > 0)
            return Misc.random(minAmount, maxAmount);
        if (!quantity.contains("-"))
            return Integer.valueOf(quantity);
        String[] amounts = quantity.split("-");
        minAmount = Integer.valueOf(amounts[0]);
        maxAmount = Integer.valueOf(amounts[1]);
        return Misc.random(minAmount, maxAmount);
    }

    public String getQuantityForDisplay() {
        return quantity;
    }

    public boolean alwaysDrop() {
        if (ignore())
            return false;
        return rarity == 1.0D;
    }
    public boolean ignore() {
        boolean ignore = false;
        /** Cheap fix for preventing clue caskets being dropped at 1/1 **/
        String[] names = {"Reward casket"};
        for (String ignoreNames : names) {
            if (name.contains(ignoreNames))
                ignore = true;
        }
        return ignore;
    }

    public OSBoxNPCDrops(int id, String name, boolean members, String quantity, boolean noted, double rarity, int rolls) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.quantity = quantity;
        this.noted = noted;
        this.rarity = rarity;
        this.rolls = rolls;
        this.getQuantity();
    }

    public static String formatRates(double rarity) {
        DecimalFormat df = new DecimalFormat("#.##########"); //<--- shows true value of the double..
        /** TODO **/
        return "1/"+df.format(rarity);
    }
}
