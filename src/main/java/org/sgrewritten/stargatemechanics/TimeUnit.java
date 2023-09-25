package org.sgrewritten.stargatemechanics;

public enum TimeUnit {
    TICK(50, 't'),
    SECOND(1000,'s'),
    MINUTE(SECOND.time*60, 'm'),
    HOUR(MINUTE.time*60, 'h'),
    DAY(HOUR.time*24, 'd'),
    WEEK(DAY.time*7, 'w'),
    YEAR(DAY.time*365, 'y');

    private final long time;
    private final char representativeCharacter;

    TimeUnit(long time, char representativeCharacter) {
        this.time = time;
        this.representativeCharacter = representativeCharacter;
    }

    public long getTime(){
        return this.time;
    }

    public char getRepresentativeCharacter(){
        return this.representativeCharacter;
    }

    public static TimeUnit valueOf(char representativeCharacter){
        for(TimeUnit timeUnit : TimeUnit.values()){
            if(timeUnit.getRepresentativeCharacter() == representativeCharacter){
                return timeUnit;
            }
        }
        throw new IllegalArgumentException();
    }
}
