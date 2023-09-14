package org.sgrewritten.stargatemechanics.locale;

import org.bukkit.entity.Entity;

public class MessageSender {

    public static void sendMessage(Entity target, String message){
        target.sendMessage(message);
    }
}
