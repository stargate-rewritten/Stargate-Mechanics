package org.sgrewritten.stargatemechanics.redstone;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.sgrewritten.stargate.api.gate.GateStructureType;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.utils.VectorUtils;
import org.sgrewritten.stargatemechanics.utils.redstone.RedstoneWireUtils;

import java.util.*;
import java.util.logging.Level;

/**
 * Redstone engine that decides the state of a gate based of or condition of all the signals
 */
public class OrRedstoneEngine implements RedstoneEngine{

    private final RegistryAPI registry;
    private Map<RealPortal, Set<BlockLocation>> activeSignalsMap = new HashMap<>();
    private Map<BlockLocation, Set<RealPortal>> relevantPositionsMap = new HashMap<>();

    public OrRedstoneEngine(RegistryAPI registry){
        this.registry = Objects.requireNonNull(registry);
    }
    @Override
    public void handleSignal(Block block, int oldCurrent, int newCurrent) {
        // Filter away any changes in current that only modifies the signal strength
        if((newCurrent == 0) == (oldCurrent == 0)){
            StargateMechanics.log(Level.INFO, "Ping 1");
            return;
        }
        if(newCurrent == 0){
            Set<RealPortal> portals = getPortalsFromPosition(new BlockLocation(block.getLocation()));
            if(portals == null || portals.isEmpty()){
                StargateMechanics.log(Level.INFO, "Ping 2");
                return;
            }
            this.stopTrackingPosition(block.getLocation());
            for(RealPortal portal : portals){
                if(!this.isRedstoneActive(portal)){
                    StargateMechanics.log(Level.INFO, "Ping 3");
                    portal.close(true);
                }
            }
        } else {
            trackPositionsCheck(block);
        }
    }

    private void trackPositionsCheck(Block block) {
        switch (block.getType()){
            case REDSTONE_WIRE -> handleRedstoneWire(block);
            default -> {}
        }

    }

    private void handleRedstoneWire(Block block) {
        StargateMechanics.log(Level.INFO, "Ping 4");
        Set<BlockLocation> effectedBlocks = RedstoneWireUtils.getEffectedBlocksFromRedstoneWire(block);
        for(BlockLocation effectedBlock : effectedBlocks){
            if(!effectedBlock.getLocation().getBlock().getType().isSolid()){
                continue;
            }
            RealPortal portal = registry.getPortal(effectedBlock, GateStructureType.FRAME);
            if(portal != null){
                StargateMechanics.log(Level.INFO, "Ping 5");
                trackPosition(new BlockLocation(block.getLocation()),portal);
                portal.open(null);
            }
        }
    }

    private void trackPosition(BlockLocation blockLocation, RealPortal portal) {
        relevantPositionsMap.putIfAbsent(blockLocation, new HashSet<>());
        relevantPositionsMap.get(blockLocation).add(portal);
        activeSignalsMap.putIfAbsent(portal, new HashSet<>());
        activeSignalsMap.get(portal).add(blockLocation);
    }

    private boolean isRedstoneActive(RealPortal portal) {
        return !activeSignalsMap.get(portal).isEmpty();
    }

    private void stopTrackingPosition(Location location) {
        BlockLocation blockLocation = new BlockLocation(location);
        Set<RealPortal> portals = relevantPositionsMap.remove(blockLocation);
        for(RealPortal portal : portals){
            activeSignalsMap.get(portal).remove(blockLocation);
        }
    }

    private Set<RealPortal> getPortalsFromPosition(BlockLocation blockLocation) {
        return relevantPositionsMap.get(blockLocation);
    }

    @Override
    public void handleBlockPlace(Block block) {
        handleBlockChange(block);
    }

    private void handleBlockChange(Block block){
        for(BlockVector adjacent : VectorUtils.getAdjacentVectors()){
            Location adjacentLocation = block.getLocation().clone().add(adjacent);
            if(relevantPositionsMap.get(new BlockLocation(adjacentLocation)) != null){
                stopTrackingPosition(adjacentLocation);
                trackPositionsCheck(adjacentLocation.getBlock());
            }
        }
    }

    @Override
    public void handleBlockBreak(Block block) {
        handleBlockChange(block);
    }

    @Override
    public void handleMultiBlockMove(List<Block> blocks, Vector moveDirection) {

    }
}
