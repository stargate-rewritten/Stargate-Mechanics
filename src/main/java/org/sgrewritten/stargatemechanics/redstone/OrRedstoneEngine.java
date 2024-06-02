package org.sgrewritten.stargatemechanics.redstone;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.block.data.type.Switch;
import org.sgrewritten.stargate.api.gate.GateStructureType;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.utils.redstone.RedstoneWireUtils;

import java.util.*;
import java.util.logging.Level;

/**
 * Redstone engine that decides the state of a gate based of or condition of all the signals
 */
public class OrRedstoneEngine implements RedstoneEngine {

    private final RegistryAPI registry;
    private final StargateMechanics stargateMechanics;
    private Map<RealPortal, Set<BlockLocation>> activeSignalsMap = new HashMap<>();
    private Map<BlockLocation, Set<RealPortal>> relevantPositionsMap = new HashMap<>();

    public OrRedstoneEngine(RegistryAPI registry, StargateMechanics stargateMechanics) {
        this.registry = Objects.requireNonNull(registry);
        this.stargateMechanics = stargateMechanics;
    }

    @Override
    public void handleSignal(Block block, int oldCurrent, int newCurrent) {
        // Filter away any changes in current that only modifies the signal strength
        if ((newCurrent == 0) == (oldCurrent == 0)) {
            return;
        }
        if (newCurrent == 0) {
            Set<RealPortal> portals = getPortalsFromPosition(new BlockLocation(block.getLocation()));
            if (portals == null || portals.isEmpty()) {
                return;
            }
            this.stopTrackingPosition(block.getLocation());
            for (RealPortal portal : portals) {
                if (!this.isRedstoneActive(portal)) {
                    this.updatePortalState(portal);
                }
            }
        } else {
            trackPositionsCheck(block);
        }
    }

    /**
     * Check if the powered block is a redstone block, and whether it can interact with any portal
     *
     * @param block <p>The powered block</p>
     */
    private void trackPositionsCheck(Block block) {
        if (Tag.BUTTONS.isTagged(block.getType())) {
            handleSwitch(block);
            return;
        }

        switch (block.getType()) {
            case REDSTONE_WIRE -> handleRedstoneWire(block);
            case REPEATER, OBSERVER, COMPARATOR -> handleDirectional(block);
            case LEVER -> handleSwitch(block);
            case REDSTONE_TORCH, REDSTONE_WALL_TORCH, TRIPWIRE_HOOK -> checkRelativePosition(block, BlockFace.UP);
            case TRAPPED_CHEST, LECTERN -> checkRelativePosition(block, BlockFace.DOWN);
            default -> {
            }
        }

    }

    private void checkRelativePosition(Block block, BlockFace face) {
        Block effectedBlock = block.getRelative(face);
        RealPortal portal = registry.getPortal(effectedBlock.getLocation(), GateStructureType.FRAME);
        if (portal != null) {
            trackPosition(new BlockLocation(block.getLocation()), portal);
            this.updatePortalState(portal);
        }
    }

    private void handleSwitch(Block block) {
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Switch switchData)) {
            return;
        }
        switch (switchData.getAttachedFace()) {
            case WALL -> handleDirectional(block);
            case FLOOR -> checkRelativePosition(block, BlockFace.DOWN);
            case CEILING -> checkRelativePosition(block, BlockFace.UP);
        }
    }

    private void handleDirectional(Block block) {
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Directional directional)) {
            return;
        }
        checkRelativePosition(block, directional.getFacing().getOppositeFace());
    }

    private void handleRedstoneWire(Block block) {
        Set<BlockLocation> effectedBlocks = RedstoneWireUtils.getEffectedBlocksFromRedstoneWire(block);
        for (BlockLocation effectedBlock : effectedBlocks) {
            if (!effectedBlock.getLocation().getBlock().getType().isSolid()) {
                continue;
            }
            RealPortal portal = registry.getPortal(effectedBlock, GateStructureType.FRAME);
            if (portal != null) {
                trackPosition(new BlockLocation(block.getLocation()), portal);
                this.updatePortalState(portal);
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
        for (RealPortal portal : portals) {
            if (!isRedstoneActive(portal)) {
                this.updatePortalState(portal);
            }
        }
    }

    @Override
    public void updatePortalState(RealPortal portal) {
        if (isRedstoneActive(portal)) {
            if (!portal.isOpen() && portal.getBehavior().getDestination() != null) {
                portal.open(null);
            }
        } else {
            if (portal.isOpen()) {
                portal.close(true);
            }
        }
    }

    /**
     * @param portal
     * @return <p>Whether the current portal is currently activated by redstone</p>
     */
    private boolean isRedstoneActive(RealPortal portal) {
        return !activeSignalsMap.get(portal).isEmpty();
    }

    /**
     * Redstone deactivated at the current location? Use this code to register that
     *
     * @param location
     * @return <p>All portals that were unregistered from specified location</p>
     */
    private Set<RealPortal> stopTrackingPosition(Location location) {
        BlockLocation blockLocation = new BlockLocation(location);
        Set<RealPortal> portals = relevantPositionsMap.remove(blockLocation);
        if (portals == null) {
            return new HashSet<>();
        }
        for (RealPortal portal : portals) {
            activeSignalsMap.get(portal).remove(blockLocation);
        }
        return portals;
    }


    private Set<RealPortal> getPortalsFromPosition(BlockLocation blockLocation) {
        return relevantPositionsMap.get(blockLocation);
    }

    @Override
    public void onBlockChange(Block block, BlockData changedBlockData) {
        /*
         * The only block that change how it conducts redstone depends on its block state
         * is REDSTONE_WIRE
         */
        if (block.getType() != Material.REDSTONE_WIRE || block.getBlockPower() == 0
                || (changedBlockData instanceof RedstoneWire redstoneWire && redstoneWire.getPower() != block.getBlockPower())) {
            return;
        }
        Set<RealPortal> portals = stopTrackingPosition(block.getLocation());
        trackPositionsCheck(block);
        for (RealPortal portal : portals) {
            if (!isRedstoneActive(portal)) {
                this.updatePortalState(portal);
            }
        }
    }

    /**
     * Track what happens when a block is placed
     */
    @Override
    public void onBlockPlace(Block block) {
        /*
         * The only real block that gives of a redstone signal without creating a BlockRedstoneEvent on creation
         * is the redstone torch, thereby we're excluding all other blocks from further unnecessary processing
         */
        if (!(block.getType() == Material.REDSTONE_TORCH || block.getType() == Material.REDSTONE_WALL_TORCH)) {
            return;
        }
        trackPositionsCheck(block);
    }
}
