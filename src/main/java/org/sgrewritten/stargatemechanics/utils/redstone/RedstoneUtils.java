package org.sgrewritten.stargatemechanics.utils.redstone;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.sgrewritten.stargate.api.gate.GateStructureType;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;
import org.sgrewritten.stargatemechanics.utils.PortalUtil;
import org.sgrewritten.stargatemechanics.utils.VectorUtils;

import java.util.HashSet;
import java.util.Set;

public class RedstoneUtils {

    public static void loadPortal(RealPortal portal, RedstoneEngine engine) {
        Set<BlockLocation> locationsToCheckFor = new HashSet<>();
        for (BlockLocation location : portal.getGate().getLocations(GateStructureType.FRAME)) {
            Block block = location.getLocation().getBlock();
            if (block.getBlockPower() > 0) {
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

    public static void loadPortals(RegistryAPI registry, RedstoneEngine engine) {
        PortalUtil.getAllPortals(registry).filter(realPortal -> realPortal.hasFlag(MechanicsFlag.REDSTONE_POWERED))
                .forEach(realPortal -> RedstoneUtils.loadPortal(realPortal, engine));
    }
}
