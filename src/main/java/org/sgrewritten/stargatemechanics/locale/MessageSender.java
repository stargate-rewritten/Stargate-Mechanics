package org.sgrewritten.stargatemechanics.locale;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MessageSender {

    public static void sendMessage(Entity target, String message) {
        target.sendMessage(message);
    }

    public static void sendMessage(OfflinePlayer offlinePlayer, String message) {
        if (offlinePlayer instanceof Player player) {
            sendMessage((Entity) player, message);
        }
    }
}
