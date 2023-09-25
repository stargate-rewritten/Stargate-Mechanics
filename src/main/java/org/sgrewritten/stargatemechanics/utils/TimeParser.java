package org.sgrewritten.stargatemechanics.utils;

import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.TimeUnit;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {
    private static final Pattern SEPARATED_NUMBER_WITH_TIME_UNITS = Pattern.compile(
            "(([+]?(?:\\d+\\.?|\\d*\\.\\d+))(?:[Ee][+-]?\\d+)?)([tsmhdwy])");
    /**
     * Return time in milliseconds from now
     * @param timeString
     * @return
     */
    public static long parseTime(String timeString){
        Matcher matcher = SEPARATED_NUMBER_WITH_TIME_UNITS.matcher(timeString);
        if(!matcher.find()){
            return TimeUnit.TICK.getTime()*NumberParser.parseLong(timeString);
        } else {
            long counter = parseTimeUnits(matcher);
            while(matcher.find()){
                counter += parseTimeUnits(matcher);
            }
            return counter;
        }
    }

    private static long parseTimeUnits(Matcher matcher){
        TimeUnit timeUnit = TimeUnit.valueOf(matcher.group(3).toLowerCase().toCharArray()[0]);
        return (long) (timeUnit.getTime()*NumberParser.parseDouble(matcher.group(1)));
    }
}
