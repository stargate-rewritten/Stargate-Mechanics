package org.sgrewritten.stargatemechanics.portal;

public enum MechanicsFlag {

    REDSTONE_POWERED('E',true),
    COORD('G',true),
    RANDOM_COORD('J', true),
    OPEN_TIMER('C', true),
    DESTROY_TIMER('D', true);
    

    private final char characterRepresentation;
    private final boolean userSpecifiable;

    private MechanicsFlag(char characterRepresentation, boolean userSpecifiable) {
        this.characterRepresentation = characterRepresentation;
        this.userSpecifiable = userSpecifiable;
    }

    public char getCharacterRepresentation(){
        return characterRepresentation;
    }

    public boolean isUserSpecifiable(){
        return userSpecifiable;
    }

}
