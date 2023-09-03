package org.sgrewritten.stargatemechanics.utils;

import org.bukkit.util.BlockVector;

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
}
