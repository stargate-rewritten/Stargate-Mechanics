package org.sgrewritten.stargatemechanics.utils;

import java.util.Collection;

public class LocalizedMessageFormatter {

    public static String insertFlags(String unformattedMsg, Collection<Character> flags){
        StringBuilder flagStringBuilder = new StringBuilder();
        for(Character flag : flags){
            flagStringBuilder.append(flag);
        }
        return unformattedMsg.replace("%flags%", flagStringBuilder.toString());
    }
}
