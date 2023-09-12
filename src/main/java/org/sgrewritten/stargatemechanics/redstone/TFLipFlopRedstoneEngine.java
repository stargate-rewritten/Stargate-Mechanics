package org.sgrewritten.stargatemechanics.redstone;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.api.network.portal.RealPortal;

import java.util.List;

/**
 * Here as a placeholder for the moment
 */
public class TFLipFlopRedstoneEngine implements RedstoneEngine{

    public TFLipFlopRedstoneEngine(){
        throw new UnsupportedOperationException();
    }
    @Override
    public void handleSignal(Block block, int oldCurrent, int newCurrent) {

    }

    @Override
    public void trackPosition(BlockLocation blockLocation, RealPortal portal) {

    }

    @Override
    public void updatePortalState(RealPortal portal) {

    }

    @Override
    public void handleBlockPlace(Block block) {

    }

    @Override
    public void handleBlockBreak(Block block) {

    }

    @Override
    public void handleMultiBlockMove(List<Block> blocks, Vector moveDirection) {

    }
}
