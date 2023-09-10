package org.sgrewritten.stargatemechanics.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.sgrewritten.stargate.api.network.portal.PositionType;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.event.StargateCreateEvent;
import org.sgrewritten.stargate.api.network.portal.PortalFlag;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.utils.NoSignUtils;

import java.util.ArrayList;
import java.util.List;

public class StargateEventListener implements Listener{

    private final RegistryAPI registry;
    private StargateMechanics plugin;

    public StargateEventListener(StargateMechanics plugin, RegistryAPI registry) {
        this.plugin = plugin;
        this.registry = registry;
    }
    
    @EventHandler
    public void onStargateCreate(StargateCreateEvent event) {
        if(!(event.getPortal() instanceof RealPortal)) {
            return;
        }
        if(event.getPortal().hasFlag(PortalFlag.NO_SIGN)) {
            NoSignUtils.removeSignsFromPortal((RealPortal) event.getPortal());
        }
    }
}
