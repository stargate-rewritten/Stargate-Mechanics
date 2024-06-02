package org.sgrewritten.stargatemechanics;

public enum ConfigurationOption {
    LOCALE("locale"),

    RANDOM_GENERATED_GATE_FLAG_STRING("gates.generation.jumpGateDestinationFlags"),

    GENERATED_GATE_FLAG_STRING("gates.generation.generateGateDestinationFlags"),

    DISABLED_FLAGS("gates.disabledFlags"),

    GATE_FORMAT_SELECTION_BEHAVIOR("gates.generation.gateformatSelectionMethod"),

    SPECIFIED_GATE_FORMAT_NAME("gates.generation.specifiedGateFormat");

    private final String key;

    ConfigurationOption(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
