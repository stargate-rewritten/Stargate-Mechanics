package org.sgrewritten.stargatemechanics.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;

public class BlockEventListener implements Listener{
    private final RegistryAPI registry;
    private final StargateMechanics plugin;
    private final RedstoneEngine engine;

    public BlockEventListener(RegistryAPI registry, StargateMechanics plugin, RedstoneEngine engine){
        this.registry = registry;
        this.plugin = plugin;
        this.engine = engine;
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        engine.handleSignal(event.getBlock(), event.getOldCurrent(), event.getNewCurrent());
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event){
        engine.onBlockChange(event.getBlock());
    }

    @EventHandler
    public void onBlockPlace(BlockPhysicsEvent event){
        engine.onBlockPlace(event.getBlock());
    }

    @EventHandler
    public void onBlockDestroy(BlockDestroyEvent event){
        engine.stopPositionTracking(event.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        engine.stopPositionTracking(event.getBlock().getLocation());
    }
}
