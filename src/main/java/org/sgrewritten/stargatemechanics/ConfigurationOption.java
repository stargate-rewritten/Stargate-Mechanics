package org.sgrewritten.stargatemechanics;

public enum ConfigurationOption {
    LOCALE("locale"),

    RANDOM_GENERATED_GATE_FLAG_STRING("gates.generation.jumpGateDestinationFlags"),

    GENERATED_GATE_FLAG_STRING("gates.generation.generateGateDestinationFlags"),

    DISABLED_FLAGS("gates.disabledFlags");

    private final String key;

    ConfigurationOption(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
