package org.sgrewritten.stargatemechanics.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;
import org.sgrewritten.stargatemechanics.redstone.RedstoneFlagHandler;

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

}
