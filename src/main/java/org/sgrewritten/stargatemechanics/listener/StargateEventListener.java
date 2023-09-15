package org.sgrewritten.stargatemechanics.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.sgrewritten.stargate.api.event.gate.StargateSignFormatGateEvent;
import org.sgrewritten.stargate.api.event.portal.*;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.network.portal.PortalFlag;
import org.sgrewritten.stargate.api.network.portal.format.SignLine;
import org.sgrewritten.stargate.api.network.portal.format.SignLineType;
import org.sgrewritten.stargate.api.permission.BypassPermission;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.exception.ParseException;
import org.sgrewritten.stargatemechanics.locale.LanguageManager;
import org.sgrewritten.stargatemechanics.locale.LocalizedMessageType;
import org.sgrewritten.stargatemechanics.locale.MessageSender;
import org.sgrewritten.stargatemechanics.metadata.MetaData;
import org.sgrewritten.stargatemechanics.metadata.MetaDataWriter;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;
import org.sgrewritten.stargatemechanics.signcoloring.ColorOverride;
import org.sgrewritten.stargatemechanics.signcoloring.ColorOverrideFormatter;
import org.sgrewritten.stargatemechanics.signcoloring.ColoringOverrideRegistry;
import org.sgrewritten.stargatemechanics.utils.ButtonUtils;
import org.sgrewritten.stargatemechanics.locale.LocalizedMessageFormatter;
import org.sgrewritten.stargatemechanics.utils.SignUtils;
import org.sgrewritten.stargatemechanics.utils.redstone.RedstoneUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StargateEventListener implements Listener{

    private final RegistryAPI registry;
    private final RedstoneEngine engine;
    private final ColoringOverrideRegistry coloringOverrideRegistry;
    private final LanguageManager languageManager;
    private StargateMechanics plugin;

    public StargateEventListener(StargateMechanics plugin, RegistryAPI registry, RedstoneEngine engine,
                                 ColoringOverrideRegistry coloringOverrideRegistry, LanguageManager languageManager) {
        this.plugin = plugin;
        this.registry = registry;
        this.engine = engine;
        this.coloringOverrideRegistry = coloringOverrideRegistry;
        this.languageManager = languageManager;
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
            if(event.getPortal().hasFlag(PortalFlag.NETWORKED) || event.getPortal().hasFlag(PortalFlag.ALWAYS_ON)){
                event.removeFlag(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation());
                String unformattedMsg = languageManager.getLocalizedMsg(LocalizedMessageType.FLAG_REMOVED);
                MessageSender.sendMessage(event.getEntity(), LocalizedMessageFormatter.insertFlags(unformattedMsg,
                        List.of(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation())));
            }
        }
        if(event.getPortal().hasFlag(MechanicsFlag.COORD.getCharacterRepresentation())){
            if(!event.getPortal().hasFlag(PortalFlag.FIXED)){
                String unformattedMsg = languageManager.getLocalizedMsg(LocalizedMessageType.FLAG_REMOVED);
                MessageSender.sendMessage(event.getEntity(), LocalizedMessageFormatter.insertFlags(unformattedMsg,
                        List.of(MechanicsFlag.COORD.getCharacterRepresentation())));
            }
            insertCoordinateStringFromLine(realPortal,event.getLine(3),MechanicsFlag.COORD.getCharacterRepresentation());
        }
        if(event.getPortal().hasFlag(MechanicsFlag.RANDOM_COORD.getCharacterRepresentation())){
            insertCoordinateStringFromLine(realPortal,event.getLine(3),MechanicsFlag.RANDOM_COORD.getCharacterRepresentation());
        }

    }

    public void insertCoordinateStringFromLine(RealPortal realPortal, String line, Character flagCharacter){
        String metaData = realPortal.getMetaData();
        try {
            realPortal.setMetaData(MetaDataWriter.addMetaData(MetaData.DESTINATION_COORDS,
                    findFlagInstructionsString(line,
                            MechanicsFlag.COORD.getCharacterRepresentation()),metaData));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String findFlagInstructionsString(String line, Character flagCharacter){
        Pattern pattern = Pattern.compile("(?<="+flagCharacter+"\\{)(.*)(?=\\})");
        Matcher matcher = pattern.matcher(line.toUpperCase());
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onStargateSignFormatGateEvent(StargateSignFormatGateEvent event){
        ColorOverride colorOverride = coloringOverrideRegistry.getColorOverride(event.getSign().getLocation());
        for(SignLine line : event.getLines()) {
            ColorOverrideFormatter.formatFromOverride(colorOverride, line);
            if(line.getType() == SignLineType.NETWORK) {
                RealPortal portal = registry.getPortalFromPortalPosition(event.getPortalPosition());
                if (portal.hasFlag(PortalFlag.HIDE_NETWORK)){
                    line.getComponents().clear();
                }
            }
        }
    }

    @EventHandler
    public void onStargateListPortalEvent(StargateListPortalEvent event){
        if(event.getPortal().hasFlag(PortalFlag.FORCE_SHOW)){
            event.setDeny(false);
            return;
        }
        if(event.getPortal().hasFlag(PortalFlag.HIDDEN) && !playerCanSeeHiddenPortal(event.getListedPortal(),(Player) event.getEntity())){
            event.setDeny(true);
        }
    }

    private boolean playerCanSeeHiddenPortal(Portal portalToSee, Player player) {
        return player != null && (player.hasPermission(BypassPermission.HIDDEN.getPermissionString())
                || portalToSee.getOwnerUUID().equals(player.getUniqueId()));
    }

    @EventHandler
    public void onStargateSendMessagePortalEvent(StargateSendMessagePortalEvent event){
        if(event.getPortal().hasFlag(PortalFlag.SILENT)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onStargateOpenPortalEvent(StargateOpenPortalEvent event){
        if(event.getPortal().hasFlag(MechanicsFlag.COORD.getCharacterRepresentation())){

        }
    }
}
