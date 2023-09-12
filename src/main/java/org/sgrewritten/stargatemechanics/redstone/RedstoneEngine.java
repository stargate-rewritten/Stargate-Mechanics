package org.sgrewritten.stargatemechanics.redstone;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.api.network.portal.RealPortal;

public interface RedstoneEngine {

    void handleSignal(Block block, int oldCurrent, int newCurrent);

    void trackPosition(BlockLocation blockLocation, RealPortal portal);

    void stopPositionTracking(Location location);

    void updatePortalState(RealPortal portal);

    void onBlockChange(Block block);

    void onBlockPlace(Block block);
}
