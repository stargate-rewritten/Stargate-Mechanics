package org.sgrewritten.stargatemechanics.redstone;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Switch;
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
        StargateMechanics.log(Level.INFO, block.getType().name());

        if(Tag.BUTTONS.isTagged(block.getType())){
            handleSwitch(block);
            return;
        }

        switch (block.getType()){
            case REDSTONE_WIRE -> handleRedstoneWire(block);
            case REPEATER, OBSERVER, COMPARATOR -> handleDirectional(block);
            case LEVER -> handleSwitch(block);
            case REDSTONE_TORCH, REDSTONE_WALL_TORCH, TRIPWIRE_HOOK -> checkRelativePosition(block,BlockFace.UP);
            case TRAPPED_CHEST, LECTERN -> checkRelativePosition(block, BlockFace.DOWN);
            default -> {}
        }

    }

    private void checkRelativePosition(Block block, BlockFace face){
        Block effectedBlock = block.getRelative(face);
        RealPortal portal = registry.getPortal(effectedBlock.getLocation(), GateStructureType.FRAME);
        if(portal != null){
            trackPosition(new BlockLocation(block.getLocation()),portal);
            portal.open(null);
        }
    }
    private void handleSwitch(Block block){
        BlockData blockData = block.getBlockData();
        if(!(blockData instanceof Switch switchData)){
            StargateMechanics.log(Level.INFO,"ping 1");
            return;
        }
        switch (switchData.getAttachedFace()){
            case WALL -> handleDirectional(block);
            case FLOOR -> checkRelativePosition(block,BlockFace.DOWN);
            case CEILING -> checkRelativePosition(block,BlockFace.UP);
        }
    }

    private void handleDirectional(Block block){
        BlockData blockData = block.getBlockData();
        if(!(blockData instanceof Directional directional)){
            return;
        }
        checkRelativePosition(block,directional.getFacing().getOppositeFace());
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
        relevantPositionsMap.putIfAbsent(blockLocation, new HashSet<>());
        relevantPositionsMap.get(blockLocation).add(portal);
        activeSignalsMap.putIfAbsent(portal, new HashSet<>());
        activeSignalsMap.get(portal).add(blockLocation);
    }

    @Override
    public void stopPositionTracking(Location location) {
        Set<RealPortal> portals = stopTrackingPosition(location);
        for(RealPortal portal : portals){
            if(!isRedstoneActive(portal)){
                portal.close(true);
            }
        }
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

    public Set<RealPortal> stopTrackingPosition(Location location) {
        BlockLocation blockLocation = new BlockLocation(location);
        Set<RealPortal> portals = relevantPositionsMap.remove(blockLocation);
        if(portals == null){
            return new HashSet<>();
        }
        for(RealPortal portal : portals){
            activeSignalsMap.get(portal).remove(blockLocation);
        }
        return  portals;
    }

    private Set<RealPortal> getPortalsFromPosition(BlockLocation blockLocation) {
        return relevantPositionsMap.get(blockLocation);
    }

    private void handleBlockChange(Block block){
        for(BlockFace adjacent : VectorUtils.getNearBlockFaces()){
            Block adjacentBlock = block.getRelative(adjacent);
            Set<RealPortal> portals = relevantPositionsMap.get(new BlockLocation(adjacentBlock.getLocation()));
            if(portals != null && !portals.isEmpty()){
                stopTrackingPosition(adjacentBlock.getLocation());
                trackPositionsCheck(adjacentBlock);
                for(RealPortal portal : portals){
                    if(!this.isRedstoneActive(portal)){
                        portal.close(true);
                    } else {
                        portal.open(null);
                    }
                }
            }
        }

    }

    @Override
    public void onBlockChange(Block block){
        if(block.getType() != Material.REDSTONE_WIRE || block.getBlockPower() == 0){
            return;
        }

        Set<RealPortal> portals = stopTrackingPosition(block.getLocation());
        trackPositionsCheck(block);
        for(RealPortal portal : portals){
            if(!isRedstoneActive(portal)){
                portal.close(true);
            }
        }
    }

    @Override
    public void onBlockPlace(Block block){
        if( !(block.getType() == Material.REDSTONE_TORCH || block.getType() == Material.REDSTONE_WALL_TORCH)){
            return;
        }


        Set<RealPortal> portals = stopTrackingPosition(block.getLocation());
        trackPositionsCheck(block);
        for(RealPortal portal : portals){
            if(!isRedstoneActive(portal)){
                portal.close(true);
            }
        }
    }
}
