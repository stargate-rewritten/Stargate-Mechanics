package org.sgrewritten.stargatemechanics.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargate.api.event.gate.StargateSignFormatGateEvent;
import org.sgrewritten.stargate.api.event.portal.StargateClosePortalEvent;
import org.sgrewritten.stargate.api.event.portal.StargateCreatePortalEvent;
import org.sgrewritten.stargate.api.event.portal.StargateListPortalEvent;
import org.sgrewritten.stargate.api.event.portal.StargateOpenPortalEvent;
import org.sgrewritten.stargate.api.event.portal.StargateSignDyeChangePortalEvent;
import org.sgrewritten.stargate.api.event.portal.message.StargateSendMessagePortalEvent;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.gate.GateFormatAPI;
import org.sgrewritten.stargate.api.gate.GateFormatRegistry;
import org.sgrewritten.stargate.api.network.PortalBuilder;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.PortalFlag;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.network.portal.format.SignLine;
import org.sgrewritten.stargate.api.network.portal.format.SignLineType;
import org.sgrewritten.stargate.api.permission.BypassPermission;
import org.sgrewritten.stargate.exception.GateConflictException;
import org.sgrewritten.stargate.exception.InvalidStructureException;
import org.sgrewritten.stargate.exception.NoFormatFoundException;
import org.sgrewritten.stargate.exception.TranslatableException;
import org.sgrewritten.stargatemechanics.ConfigurationOption;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.exception.GateGenerationFault;
import org.sgrewritten.stargatemechanics.exception.ParseException;
import org.sgrewritten.stargatemechanics.generation.PortalPositionFinder;
import org.sgrewritten.stargatemechanics.locale.LanguageManager;
import org.sgrewritten.stargatemechanics.locale.LocalizedMessageFormatter;
import org.sgrewritten.stargatemechanics.locale.LocalizedMessageType;
import org.sgrewritten.stargatemechanics.locale.MessageSender;
import org.sgrewritten.stargatemechanics.metadata.MetaData;
import org.sgrewritten.stargatemechanics.metadata.MetaDataReader;
import org.sgrewritten.stargatemechanics.metadata.MetaDataWriter;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;
import org.sgrewritten.stargatemechanics.signcoloring.ColorOverride;
import org.sgrewritten.stargatemechanics.signcoloring.ColorOverrideFormatter;
import org.sgrewritten.stargatemechanics.signcoloring.ColoringOverrideRegistry;
import org.sgrewritten.stargatemechanics.utils.ButtonUtils;
import org.sgrewritten.stargatemechanics.utils.CoordinateParser;
import org.sgrewritten.stargatemechanics.utils.SignUtils;
import org.sgrewritten.stargatemechanics.utils.TimeParser;
import org.sgrewritten.stargatemechanics.utils.redstone.RedstoneUtils;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StargateEventListener implements Listener {

    private final StargateAPI stargateAPI;
    private final RedstoneEngine engine;
    private final ColoringOverrideRegistry coloringOverrideRegistry;
    private final LanguageManager languageManager;
    private StargateMechanics plugin;

    public StargateEventListener(StargateMechanics plugin, StargateAPI stargateAPI, RedstoneEngine engine,
                                 ColoringOverrideRegistry coloringOverrideRegistry, LanguageManager languageManager) {
        this.plugin = plugin;
        this.stargateAPI = stargateAPI;
        this.engine = engine;
        this.coloringOverrideRegistry = coloringOverrideRegistry;
        this.languageManager = languageManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onStargateCreate(StargateCreatePortalEvent event) {
        if (!(event.getPortal() instanceof RealPortal realPortal)) {
            return;
        }
        if (event.getPortal().hasFlag(PortalFlag.NO_SIGN)) {
            SignUtils.removeSignsFromPortal(realPortal);
        }
        if (event.getPortal().hasFlag(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation())) {
            ButtonUtils.removeButtonsFromPortal(realPortal);
            RedstoneUtils.loadPortal(realPortal, engine);
            if (event.getPortal().hasFlag(PortalFlag.NETWORKED) || event.getPortal().hasFlag(PortalFlag.ALWAYS_ON)) {
                event.removeFlag(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation());
                String unformattedMsg = languageManager.getLocalizedMsg(LocalizedMessageType.FLAG_REMOVED_INCOMPATIBLE);
                MessageSender.sendMessage(event.getEntity(), LocalizedMessageFormatter.insertFlags(unformattedMsg,
                        List.of(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation())));
            }
        }
        if (event.getPortal().hasFlag(MechanicsFlag.COORD.getCharacterRepresentation())) {
            if (!event.getPortal().hasFlag(PortalFlag.FIXED) || event.getPortal().hasFlag(MechanicsFlag.RANDOM_COORD.getCharacterRepresentation())) {
                String unformattedMsg = languageManager.getLocalizedMsg(LocalizedMessageType.FLAG_REMOVED_INCOMPATIBLE);
                MessageSender.sendMessage(event.getEntity(), LocalizedMessageFormatter.insertFlags(unformattedMsg,
                        List.of(MechanicsFlag.COORD.getCharacterRepresentation())));
            } else {
                insertCoordData(realPortal, event.getLine(3), event.getEntity(), MechanicsFlag.COORD.getCharacterRepresentation());
            }
        }
        if (event.getPortal().hasFlag(MechanicsFlag.RANDOM_COORD.getCharacterRepresentation())) {
            insertCoordData(realPortal, event.getLine(3), event.getEntity(), MechanicsFlag.RANDOM_COORD.getCharacterRepresentation());
        }
        if (event.getPortal().hasFlag(MechanicsFlag.OPEN_TIMER.getCharacterRepresentation())) {
            insertTimerData(realPortal, event.getLine(3), event.getEntity(), MechanicsFlag.OPEN_TIMER.getCharacterRepresentation());
        }
        if (event.getPortal().hasFlag(MechanicsFlag.DESTROY_TIMER.getCharacterRepresentation())) {
            insertTimerData(realPortal, event.getLine(3), event.getEntity(), MechanicsFlag.DESTROY_TIMER.getCharacterRepresentation());
        }
    }


    private void insertCoordData(RealPortal portal, String line, Entity entity, Character flagCharacter) {
        String value = findFlagInstructionsString(line, flagCharacter);

        try {
            if (flagCharacter == MechanicsFlag.RANDOM_COORD.getCharacterRepresentation()) {
                CoordinateParser.getRandomLocationFromExpression(value, portal);
            } else if (flagCharacter == MechanicsFlag.COORD.getCharacterRepresentation()) {
                CoordinateParser.getLocationFromExpression(value, portal);
            } else {
                throw new UnsupportedOperationException();
            }
            insertMetaDataFromFlagArgument(portal, MetaData.DESTINATION_COORDS, value);
        } catch (ParseException e) {
            MessageSender.sendMessage(entity, languageManager.getLocalizedMsg(LocalizedMessageType.FLAG_REMOVED_INVALID_ARGUMENT));
            portal.removeFlag(flagCharacter);
        }
    }

    private void insertTimerData(RealPortal portal, String line, Entity entity, Character flagCharacter) {
        String value = findFlagInstructionsString(line, flagCharacter);
        try {
            TimeParser.parseTime(value);
            if (MechanicsFlag.DESTROY_TIMER.getCharacterRepresentation() == flagCharacter) {
                insertMetaDataFromFlagArgument(portal, MetaData.DESTROY_COUNTDOWN, value);
            } else if (MechanicsFlag.OPEN_TIMER.getCharacterRepresentation() == flagCharacter) {
                insertMetaDataFromFlagArgument(portal, MetaData.OPEN_COUNTDOWN, value);
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (ParseException | IllegalStateException e) {
            MessageSender.sendMessage(entity, languageManager.getLocalizedMsg(LocalizedMessageType.FLAG_REMOVED_INVALID_ARGUMENT));
            portal.removeFlag(flagCharacter);
        }
    }

    private void insertMetaDataFromFlagArgument(RealPortal realPortal, MetaData metaDataType, String value) {
        try {
            realPortal.setMetaData(MetaDataWriter.addMetaData(metaDataType, value, realPortal.getMetaData()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private String findFlagInstructionsString(String line, Character flagCharacter) {
        Pattern pattern = Pattern.compile("(?<=" + flagCharacter + "\\{)(.*?)(?=\\})");
        Matcher matcher = pattern.matcher(line.toUpperCase());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    @EventHandler(ignoreCancelled = true)
    public void onStargateClosePortalEvent(StargateClosePortalEvent event) {
        if (event.getPortal().hasFlag(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation()) &&
                !event.getForce()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onStargateSignDyeChangePortalEvent(StargateSignDyeChangePortalEvent event) {
        if (!(event.getPortal() instanceof RealPortal realPortal)) {
            return;
        }
        coloringOverrideRegistry.registerOverride(event.getLocation(), new ColorOverride(event.getColorChange()));
        PortalPosition portalPosition = event.getPortalPosition();
        String previousMetaData = portalPosition.getMetaData(realPortal);
        try {
            String newMetaData = MetaDataWriter.addMetaData(MetaData.SIGN_COLOR, event.getColorChange().name(), previousMetaData);
            portalPosition.setMetaData(realPortal, newMetaData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStargateSignFormatGateEvent(StargateSignFormatGateEvent event) {
        ColorOverride colorOverride = coloringOverrideRegistry.getColorOverride(event.getSign().getLocation());
        for (SignLine line : event.getLines()) {
            ColorOverrideFormatter.formatFromOverride(colorOverride, line);
            if (line.getType() == SignLineType.NETWORK) {
                RealPortal portal = stargateAPI.getRegistry().getPortalFromPortalPosition(event.getPortalPosition());
                if (portal.hasFlag(PortalFlag.HIDE_NETWORK)) {
                    line.getComponents().clear();
                }
            }
        }
    }

    @EventHandler
    public void onStargateListPortalEvent(StargateListPortalEvent event) {
        if (event.getPortal().hasFlag(PortalFlag.FORCE_SHOW)) {
            event.setDeny(false);
            return;
        }
        if (event.getPortal().hasFlag(PortalFlag.HIDDEN) && !playerCanSeeHiddenPortal(event.getListedPortal(), (Player) event.getEntity())) {
            event.setDeny(true);
        }
    }

    private boolean playerCanSeeHiddenPortal(Portal portalToSee, Player player) {
        return player != null && (player.hasPermission(BypassPermission.HIDDEN.getPermissionString())
                || portalToSee.getOwnerUUID().equals(player.getUniqueId()));
    }

    @EventHandler
    public void onStargateSendMessagePortalEvent(StargateSendMessagePortalEvent event) {
        if (event.getPortal().hasFlag(PortalFlag.SILENT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onStargateOpenPortalEvent(StargateOpenPortalEvent event) throws ParseException {
        Portal portal = event.getPortal();
        if (!(portal instanceof RealPortal realPortal)) {
            return;
        }
        if (realPortal.getDestination() == null) {
            RealPortal destination = createGateFromFlagArguments(realPortal);
            if (destination != null) {
                event.setDestination(destination);
                event.setCancelled(false);
            }
        }
    }

    private RealPortal createGateFromFlagArguments(RealPortal realPortal) {
        Location destinationCoordinate = null;
        String metadata = realPortal.getMetaData();
        try {
            if (realPortal.hasFlag(MechanicsFlag.COORD.getCharacterRepresentation())) {
                StargateMechanics.log(Level.INFO, metadata);
                destinationCoordinate = CoordinateParser.getLocationFromExpression(MetaDataReader.getData(metadata, MetaData.DESTINATION_COORDS), realPortal);
            }
            if (realPortal.hasFlag(MechanicsFlag.RANDOM_COORD.getCharacterRepresentation())) {
                StargateMechanics.log(Level.INFO, metadata);
                destinationCoordinate = CoordinateParser.getRandomLocationFromExpression(MetaDataReader.getData(metadata, MetaData.DESTINATION_COORDS), realPortal);
            }
            if (destinationCoordinate != null) {
                Optional<String> gateFormatName = GateFormatRegistry.getAllGateFormatNames().stream().findAny();
                if (gateFormatName.isPresent()) {
                    GateFormatAPI gateFormatAPI = GateFormatRegistry.getFormat(gateFormatName.get());
                    PortalPositionFinder portalPositionFinder = new PortalPositionFinder(destinationCoordinate, gateFormatAPI, stargateAPI.getRegistry());
                    GateAPI gateAPi = portalPositionFinder.getValidGate(4);
                    gateAPi.forceGenerateStructure();
                    gateAPi.calculatePortalPositions(false);
                    String flagsString = plugin.getConfig().getString(ConfigurationOption.GENERATED_GATE_FLAG_STRING.getKey());
                    PortalBuilder portalBuilder = new PortalBuilder(stargateAPI, Bukkit.getOfflinePlayer(realPortal.getOwnerUUID()), flagsString == null ? "" : flagsString, realPortal.getDestinationName(),gateAPi);
                    return portalBuilder.setNetwork(realPortal.getNetwork()).build();
                }
            }
        } catch (ParseException | GateGenerationFault | InvalidStructureException | TranslatableException |
                 GateConflictException | NoFormatFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
