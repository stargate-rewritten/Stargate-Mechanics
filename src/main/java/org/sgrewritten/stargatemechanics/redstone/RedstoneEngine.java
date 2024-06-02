package org.sgrewritten.stargatemechanics.redstone;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.api.network.portal.RealPortal;

public interface RedstoneEngine {

    /**
     * Handle a redstone signal
     * @param block
     * @param oldCurrent
     * @param newCurrent
     */
    void handleSignal(Block block, int oldCurrent, int newCurrent);

    /**
     * Track this position with related portal as an active position (does not open the portal)
     * @param blockLocation
     * @param portal
     */
    void trackPosition(BlockLocation blockLocation, RealPortal portal);

    /**
     * Stop tracking this position does close the portal if there's no active positions in its proximity
     * @param location
     */
    void stopPositionTracking(Location location);

    /**
     * Update the state of the specified portal; looks at the active positions in relation to this portal
     * if no active positions are in the proximity, then it will close, otherwise it will open.
     * @param portal
     */
    void updatePortalState(RealPortal portal);

    /**
     * What should happen when the block encounters a change in state.
     * @param block
     */
    void onBlockChange(Block block, BlockData changedBlockData);

    /**
     * What should happen when a block is placed.
     * @param block
     */
    void onBlockPlace(Block block);
}
