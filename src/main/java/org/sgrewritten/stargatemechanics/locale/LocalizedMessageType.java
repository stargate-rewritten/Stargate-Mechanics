package org.sgrewritten.stargatemechanics.locale;

public enum LocalizedMessageType {
    PREFIX("prefix"),
    FLAG_REMOVED("flagRemoved");


    private final String key;

    LocalizedMessageType(String key) {
        this.key = key;
    }

    public String getKey(){
        return this.key;
    }
}
