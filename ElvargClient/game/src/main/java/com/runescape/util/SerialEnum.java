package com.runescape.util;

public interface SerialEnum {

    static SerialEnum forId(SerialEnum[] values, int id) {
        for(int index = 0; index < values.length; ++index) {
            SerialEnum value = values[index];
            if (id == value.id()) {
                return value;
            }
        }

        return null;
    }

    int id();
}
