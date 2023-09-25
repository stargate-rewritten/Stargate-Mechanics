package org.sgrewritten.stargatemechanics.utils;

public class NumberParser {

    public static int parseInt(String numbersString){
        return Double.valueOf(numbersString).intValue();
    }
    public static long parseLong(String numbersString){
        return Double.valueOf(numbersString).longValue();
    }
}
