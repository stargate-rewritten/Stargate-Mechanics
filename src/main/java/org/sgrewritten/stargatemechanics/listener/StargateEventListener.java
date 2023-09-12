package org.sgrewritten.stargatemechanics.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.sgrewritten.stargate.api.event.portal.StargateClosePortalEvent;
import org.sgrewritten.stargate.api.event.portal.StargateCreatePortalEvent;
import org.sgrewritten.stargate.api.network.portal.PositionType;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.network.portal.PortalFlag;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;
import org.sgrewritten.stargatemechanics.utils.NoSignUtils;
import org.sgrewritten.stargatemechanics.utils.redstone.RedstoneUtils;

import java.util.ArrayList;
import java.util.List;

public class StargateEventListener implements Listener{

    private final RegistryAPI registry;
    private final RedstoneEngine engine;
    private StargateMechanics plugin;

    public StargateEventListener(StargateMechanics plugin, RegistryAPI registry, RedstoneEngine engine) {
        this.plugin = plugin;
        this.registry = registry;
        this.engine = engine;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onStargateCreate(StargateCreatePortalEvent event) {
        if(!(event.getPortal() instanceof RealPortal realPortal)) {
            return;
        }
        if(event.getPortal().hasFlag(PortalFlag.NO_SIGN)) {
            NoSignUtils.removeSignsFromPortal(realPortal);
        }
        if(event.getPortal().hasFlag(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation())){
            RedstoneUtils.loadPortal(realPortal, engine);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onStargateClosePortalEvent(StargateClosePortalEvent event){
        if(event.getPortal().hasFlag(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation()) &&
                !event.getForce()){
            event.setCancelled(true);
        }
    }
}
