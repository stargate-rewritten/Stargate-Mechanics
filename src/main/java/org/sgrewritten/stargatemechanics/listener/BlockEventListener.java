package org.sgrewritten.stargatemechanics.listener;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.PositionType;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.flags.RedstoneFlagHandler;
import org.sgrewritten.stargatemechanics.metadata.MetaDataProperty;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;

import java.util.logging.Level;

public class BlockEventListener implements Listener{
    private final RegistryAPI registry;
    private final StargateMechanics plugin;

    public BlockEventListener(RegistryAPI registry, StargateMechanics plugin){
        this.registry = registry;
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Bukkit.getScheduler().runTask(plugin,new RedstoneFlagHandler(event,registry,plugin));
    }
}
