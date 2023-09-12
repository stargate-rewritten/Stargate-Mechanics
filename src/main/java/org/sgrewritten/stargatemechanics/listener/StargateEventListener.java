package org.sgrewritten.stargatemechanics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.sgrewritten.stargate.api.event.gate.StargateSignFormatGateEvent;
import org.sgrewritten.stargate.api.event.portal.StargateClosePortalEvent;
import org.sgrewritten.stargate.api.event.portal.StargateCreatePortalEvent;
import org.sgrewritten.stargate.api.event.portal.StargateSignDyeChangePortalEvent;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.network.portal.PortalFlag;
import org.sgrewritten.stargate.api.network.portal.format.SignLine;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.exception.ParseException;
import org.sgrewritten.stargatemechanics.metadata.MetaData;
import org.sgrewritten.stargatemechanics.metadata.MetaDataWriter;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;
import org.sgrewritten.stargatemechanics.signcoloring.ColorOverride;
import org.sgrewritten.stargatemechanics.signcoloring.ColorOverrideFormatter;
import org.sgrewritten.stargatemechanics.signcoloring.ColoringOverrideRegistry;
import org.sgrewritten.stargatemechanics.utils.ButtonUtils;
import org.sgrewritten.stargatemechanics.utils.SignUtils;
import org.sgrewritten.stargatemechanics.utils.redstone.RedstoneUtils;

public class StargateEventListener implements Listener{

    private final RegistryAPI registry;
    private final RedstoneEngine engine;
    private final ColoringOverrideRegistry coloringOverrideRegistry;
    private StargateMechanics plugin;

    public StargateEventListener(StargateMechanics plugin, RegistryAPI registry, RedstoneEngine engine, ColoringOverrideRegistry coloringOverrideRegistry) {
        this.plugin = plugin;
        this.registry = registry;
        this.engine = engine;
        this.coloringOverrideRegistry = coloringOverrideRegistry;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onStargateCreate(StargateCreatePortalEvent event) {
        if(!(event.getPortal() instanceof RealPortal realPortal)) {
            return;
        }
        if(event.getPortal().hasFlag(PortalFlag.NO_SIGN)) {
            SignUtils.removeSignsFromPortal(realPortal);
        }
        if(event.getPortal().hasFlag(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation())){
            ButtonUtils.removeButtonsFromPortal(realPortal);
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

    @EventHandler
    public void onStargateSignDyeChangePortalEvent(StargateSignDyeChangePortalEvent event){
        if(!(event.getPortal() instanceof RealPortal realPortal)){
            return;
        }
        coloringOverrideRegistry.registerOverride(event.getLocation(),new ColorOverride(event.getColorChange()));
        PortalPosition portalPosition = event.getPortalPosition();
        String previousMetaData = portalPosition.getMetaData(realPortal);
        try {
            String newMetaData = MetaDataWriter.addMetaData(MetaData.SIGN_COLOR, event.getColorChange().name(), previousMetaData);
            portalPosition.setMetaData(realPortal, newMetaData);
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onStargateSignFormatGateEvent(StargateSignFormatGateEvent event){
        ColorOverride colorOverride = coloringOverrideRegistry.getColorOverride(event.getSign().getLocation());
        for(SignLine line : event.getLines()) {
            ColorOverrideFormatter.formatFromOverride(colorOverride, line);
        }
    }
}
