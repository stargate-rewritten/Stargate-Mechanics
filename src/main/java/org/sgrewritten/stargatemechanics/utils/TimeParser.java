package org.sgrewritten.stargatemechanics.utils;

import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.TimeUnit;
import org.sgrewritten.stargatemechanics.exception.ParseException;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {
    private static final Pattern SEPARATED_NUMBER_WITH_TIME_UNITS = Pattern.compile(
            "(([+]?(?:\\d+\\.?|\\d*\\.\\d+))(?:[Ee][+-]?\\d+)?)([tsmhdwy])");
    private static final Pattern INVALID_INPUT = Pattern.compile(
            "([tsmhdwy]{2}|^[tsmhdwy]|\\d$|[^tsmhdwye.,0-9])");
    /**
     * Return time in milliseconds from now
     * @param timeString
     * @return
     */
    public static long parseTime(String timeString) throws ParseException {
        Matcher matcher = SEPARATED_NUMBER_WITH_TIME_UNITS.matcher(timeString.toLowerCase());
        if(!matcher.find()){
            return TimeUnit.TICK.getTime()*NumberParser.parseLong(timeString.toLowerCase());
        } else {
            Matcher invalidMatcher = INVALID_INPUT.matcher(timeString.toLowerCase());
            if(invalidMatcher.find()){
                throw new ParseException("Invalid time string");
            }
            long counter = parseTimeUnits(matcher);
            while(matcher.find()){
                counter += parseTimeUnits(matcher);
            }
            return counter;
        }
    }

    private static long parseTimeUnits(Matcher matcher) throws ParseException {
        TimeUnit timeUnit = TimeUnit.valueOf(matcher.group(3).toCharArray()[0]);
        return (long) (timeUnit.getTime()*NumberParser.parseDouble(matcher.group(1)));
    }
}
