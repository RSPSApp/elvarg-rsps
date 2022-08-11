package com.elvarg.game.entity.attributes;

public enum AttributeKey {

    DEFENSIVE_AUTOCAST("defensive_autocast", AttributeType.BOOLEAN),

    AUTOCAST_SELECTED("autocast_selected", AttributeType.BOOLEAN);

    private String saveName;
    private AttributeType type;

    AttributeKey() {

    }

    AttributeKey(String name, AttributeType persistType) {
        this.saveName = name;
        this.type = persistType;
    }

    public String saveName() {
        return saveName;
    }

    public AttributeType saveType() {
        return type;
    }

}
