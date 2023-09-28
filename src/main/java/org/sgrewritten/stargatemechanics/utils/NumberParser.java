package org.sgrewritten.stargatemechanics.utils;

import org.sgrewritten.stargatemechanics.exception.ParseException;

public class NumberParser {

    public static int parseInt(String numbersString) throws ParseException {
        try {
            return Double.valueOf(numbersString).intValue();
        } catch (NumberFormatException e){
            throw new ParseException(e.getMessage());
        }
    }
    public static long parseLong(String numbersString) throws ParseException {
        try {
            return Double.valueOf(numbersString).longValue();
        } catch (NumberFormatException e){
            throw new ParseException(e.getMessage());
        }
    }
    public static double parseDouble(String numbersString) throws ParseException {
        try {
            return Double.parseDouble(numbersString);
        } catch (NumberFormatException e){
            throw new ParseException(e.getMessage());
        }
    }
}
