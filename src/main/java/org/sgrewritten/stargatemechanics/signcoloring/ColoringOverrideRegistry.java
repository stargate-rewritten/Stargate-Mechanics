package org.sgrewritten.stargatemechanics.signcoloring;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;

import java.util.HashMap;
import java.util.Map;

public class ColoringOverrideRegistry {
    private final Map<BlockLocation, ColorOverride> overrideMap;

    public ColoringOverrideRegistry() {
        this.overrideMap = new HashMap<>();
    }

    public @Nullable ColorOverride getColorOverride(Location location){
        return overrideMap.get(new BlockLocation(location));
    }

    public void registerOverride(Location location, ColorOverride override){
        overrideMap.put(new BlockLocation(location),override);
    }

    public void unRegisterOverride(Location location){
        overrideMap.remove(new BlockLocation(location));
    }
}
