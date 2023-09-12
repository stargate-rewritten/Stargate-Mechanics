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
            return;
        }
        if(newCurrent == 0){
            Set<RealPortal> portals = getPortalsFromPosition(new BlockLocation(block.getLocation()));
            if(portals == null || portals.isEmpty()){
                return;
            }
            this.stopTrackingPosition(block.getLocation());
            for(RealPortal portal : portals){
                if(!this.isRedstoneActive(portal)){
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
        Set<BlockLocation> effectedBlocks = RedstoneWireUtils.getEffectedBlocksFromRedstoneWire(block);
        for(BlockLocation effectedBlock : effectedBlocks){
            if(!effectedBlock.getLocation().getBlock().getType().isSolid()){
                continue;
            }
            RealPortal portal = registry.getPortal(effectedBlock, GateStructureType.FRAME);
            if(portal != null){
                trackPosition(new BlockLocation(block.getLocation()),portal);
                portal.open(null);
            }
        }
    }

    @Override
    public void trackPosition(BlockLocation blockLocation, RealPortal portal) {
        StargateMechanics.log(Level.INFO, String.format("Tracking position %s for portal %s:%s", blockLocation,portal.getNetwork().getName(),portal.getName()));
        relevantPositionsMap.putIfAbsent(blockLocation, new HashSet<>());
        relevantPositionsMap.get(blockLocation).add(portal);
        activeSignalsMap.putIfAbsent(portal, new HashSet<>());
        activeSignalsMap.get(portal).add(blockLocation);
    }

    @Override
    public void updatePortalState(RealPortal portal){
        if(isRedstoneActive(portal)){
            if(!portal.isOpen()){
                portal.open(null);
            }
        } else {
            if(portal.isOpen()) {
                portal.close(true);
            }
        }
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
            Set<RealPortal> portals = relevantPositionsMap.get(new BlockLocation(adjacentLocation));
            if(portals != null && !portals.isEmpty()){
                stopTrackingPosition(adjacentLocation);
                trackPositionsCheck(adjacentLocation.getBlock());
                for(RealPortal portal : portals){
                    if(this.isRedstoneActive(portal)){
                        portal.close(true);
                    }
                }
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
