package org.sgrewritten.stargatemechanics.locale;

import org.sgrewritten.stargate.api.network.portal.flag.PortalFlag;

import java.util.Collection;

public class LocalizedMessageFormatter {

    public static String insertFlags(String unformattedMsg, Collection<PortalFlag> flags){
        StringBuilder flagStringBuilder = new StringBuilder();
        for(PortalFlag flag : flags){
            flagStringBuilder.append(flag.getCharacterRepresentation());
        }
        return unformattedMsg.replace("%flags%", flagStringBuilder.toString());
    }
}
