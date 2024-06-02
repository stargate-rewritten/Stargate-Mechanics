package org.sgrewritten.stargatemechanics.locale;

public enum LocalizedMessageType {
    PREFIX("prefix"),
    FLAG_REMOVED_INCOMPATIBLE("flagRemovedIncompatible"),
    FLAG_REMOVED_INVALID_ARGUMENT("flagRemovedInvalidArgument"),
    FLAG_DISABLED("flagDisabled"),
    CLICK_BUTTON_TO("coordGateLine1"),
    ENTER_GATE_TO("coordGateAlwaysOnLine1"),
    CREATE_PORTAL("coordGateLine2");

    private final String key;

    LocalizedMessageType(String key) {
        this.key = key;
    }

    public String getKey(){
        return this.key;
    }
}
