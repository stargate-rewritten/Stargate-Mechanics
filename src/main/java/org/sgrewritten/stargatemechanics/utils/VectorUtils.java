package org.sgrewritten.stargatemechanics.utils;

import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

import java.util.List;
import java.util.Set;

public class VectorUtils {

    public static Set<BlockVector> getAdjacentVectors(){
        return Set.of(new BlockVector(1, 0, 0),
                new BlockVector(0, 1, 0),
                new BlockVector(0, 0, 1),
                new BlockVector(-1, 0, 0),
                new BlockVector(0, -1, 0),
                new BlockVector(0, 0, -1));
    }

    public static List<BlockFace> getAdjacentBlockFaces() {
        return List.of(BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST);
    }

    public static List<BlockFace> getNearBlockFaces(){
        return List.of(BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.SELF);
    }
}
