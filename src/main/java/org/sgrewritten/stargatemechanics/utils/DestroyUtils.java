package org.sgrewritten.stargatemechanics.utils;

import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.gate.GateStructureType;
import org.sgrewritten.stargate.api.network.NetworkManager;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.metadata.MetaData;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;

import java.util.List;

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
            destroy(realPortal, networkManager, plugin);
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> destroy(realPortal, networkManager, plugin), timeToDestroyMillis / 50);
    }

    private static void destroy(RealPortal portal, NetworkManager networkManager, Plugin plugin){
        networkManager.destroyPortal(portal);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            GateAPI gateAPI = portal.getGate();
            Material closedType = gateAPI.getFormat().getIrisMaterial(false);
            List<BlockLocation> frameLocations = gateAPI.getLocations(GateStructureType.FRAME);
            frameLocations.stream().map(BlockLocation::getLocation).forEach(location -> location.getBlock().setType(closedType));
            gateAPI.getPortalPositions().stream().map(PortalPosition::getRelativePositionLocation).map(gateAPI::getLocation)
                    .map(Location::getBlock).forEach(block -> block.setType(closedType));
        },10);

    }
}
