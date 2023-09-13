package org.sgrewritten.stargatemechanics;

public enum ConfigurationOption {
    USE_REDSTONE_GATES("gates.redstoneEnabled"),
    LOCALE("locale");

    private final String key;

    ConfigurationOption(String key) {
        this.key = key;
    }

    public String getKey(){
        return this.key;
    }
}
