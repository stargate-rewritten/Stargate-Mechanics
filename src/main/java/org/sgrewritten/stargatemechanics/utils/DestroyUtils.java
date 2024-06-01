package org.sgrewritten.stargatemechanics.utils;

import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.sgrewritten.stargate.api.network.NetworkManager;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.metadata.MetaData;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;

public class DestroyUtils {


    public static void register(RealPortal realPortal, NetworkManager networkManager, Plugin plugin) {
        if (!realPortal.hasFlag(MechanicsFlag.DESTROY_TIMER)) {
            return;
        }
        JsonElement jsonElement = realPortal.getMetadata(MetaData.DESTROY_TIME.name());
        if (jsonElement == null) {
            return;
        }
        long destroyTimeMillis = jsonElement.getAsLong();
        long currTimeMillis = System.currentTimeMillis();
        long timeToDestroyMillis = destroyTimeMillis - currTimeMillis;
        if (timeToDestroyMillis <= 0) {
            networkManager.destroyPortal(realPortal);
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> networkManager.destroyPortal(realPortal), timeToDestroyMillis * 50);
    }
}
