package org.sgrewritten.stargatemechanics.portal;

public enum MechanicsFlag {

    REDSTONE_POWERED('E',true),
    RANDOM_WORLD('W',true),

    COORD_PORTAL('L',true);
    

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
