package org.sgrewritten.stargatemechanics.utils.redstone;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.sgrewritten.stargate.api.gate.GateStructureType;
import org.sgrewritten.stargate.api.network.Network;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.network.StorageType;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;
import org.sgrewritten.stargatemechanics.utils.VectorUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;

public class RedstoneUtils {

    public static void loadPortal(RealPortal portal, RedstoneEngine engine){
        Set<BlockLocation> locationsToCheckFor = new HashSet<>();
        for(BlockLocation location : portal.getGate().getLocations(GateStructureType.FRAME)){
            Block block = location.getLocation().getBlock();
            if(block.getBlockPower() > 0) {
                for (BlockFace blockFace : VectorUtils.getAdjacentBlockFaces()) {
                    int redstoneCurrent = block.getBlockPower(blockFace);
                    if (redstoneCurrent > 0) {
                        Block adjacentBlock = block.getRelative(blockFace);
                        engine.handleSignal(adjacentBlock, 0, redstoneCurrent);
                    }
                }
            }
        }
    }

    public static void loadPortals(RegistryAPI registry, RedstoneEngine engine){
        Stream<Network> localStream = registry.getNetworkRegistry(StorageType.LOCAL).stream();
        Stream<Network> interserverStream = registry.getNetworkRegistry(StorageType.INTER_SERVER).stream();
        Stream<Network> allNetworks = Stream.concat(localStream,interserverStream);

        allNetworks.forEach(network -> {
            for(Portal portal : network.getAllPortals()){
                if(portal instanceof RealPortal realPortal && portal.hasFlag(MechanicsFlag.REDSTONE_POWERED)){
                    RedstoneUtils.loadPortal(realPortal,engine);
                }
            }
        });
    }
}
