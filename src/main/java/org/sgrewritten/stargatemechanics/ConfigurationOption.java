package org.sgrewritten.stargatemechanics;

public enum ConfigurationOption {
    USE_REDSTONE_GATES("gates.redstoneEnabled"),

    LOCALE("locale"),

    GENERATED_GATE_FLAG_STRING("generation.jumpGateDestinationFlags");

    private final String key;

    ConfigurationOption(String key) {
        this.key = key;
    }

    public String getKey(){
        return this.key;
    }
}
