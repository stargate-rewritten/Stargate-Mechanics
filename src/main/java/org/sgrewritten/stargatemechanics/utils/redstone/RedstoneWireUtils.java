package org.sgrewritten.stargatemechanics.utils.redstone;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RedstoneWire;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;

import java.util.HashSet;
import java.util.Set;

public class RedstoneWireUtils {

    public static Set<BlockLocation> getEffectedBlocksFromRedstoneWire(Block redstoneWireBlock){
        BlockData data = redstoneWireBlock.getBlockData();
        Set<BlockLocation> output = new HashSet<>();
        if(data instanceof RedstoneWire redstoneWire){
            Location location = redstoneWireBlock.getLocation();
            for(BlockFace face : redstoneWire.getAllowedFaces()){
                if(redstoneWire.getFace(face) == RedstoneWire.Connection.NONE){
                    continue;
                }
                Location otherLocation = location.clone().add(face.getDirection());
                output.add(new BlockLocation(otherLocation));
            }
            output.add(new BlockLocation(redstoneWireBlock.getRelative(BlockFace.DOWN).getLocation()));
        }
        return output;
    }
}
