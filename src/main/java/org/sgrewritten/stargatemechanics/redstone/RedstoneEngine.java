package org.sgrewritten.stargatemechanics.redstone;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;

public interface RedstoneEngine {

    void handleSignal(Block block, int oldCurrent, int newCurrent);

    /**
     * Necessary to keep track of, as placing a block next to a powered block can change its state
     * @param block
     */
    void handleBlockPlace(Block block);

    /**
     * Necessary to keep track of, as removing a block next to a powered block can change its state
     * @param block
     */
    void handleBlockBreak(Block block);

    /**
     * Necessary to keep track of, as moving a block next to a powered block can change its state
     * @param blocks
     * @param moveDirection
     */
    void handleMultiBlockMove(List<Block> blocks, Vector moveDirection);
}
