package org.sgrewritten.stargatemechanics.listener;

import com.google.gson.JsonPrimitive;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargate.api.event.StargatePreCreatePortalEvent;
import org.sgrewritten.stargate.api.event.portal.*;
import org.sgrewritten.stargate.api.event.portal.message.AsyncStargateSendMessagePortalEvent;
import org.sgrewritten.stargate.api.event.portal.message.StargateSendMessagePortalEvent;
import org.sgrewritten.stargate.api.event.portal.message.SyncStargateSendMessagePortalEvent;
import org.sgrewritten.stargate.api.network.PortalBuilder;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.network.portal.flag.PortalFlag;
import org.sgrewritten.stargate.api.network.portal.formatting.SignLine;
import org.sgrewritten.stargate.api.network.portal.formatting.SignLineType;
import org.sgrewritten.stargate.api.permission.BypassPermission;
import org.sgrewritten.stargate.exception.GateConflictException;
import org.sgrewritten.stargate.exception.InvalidStructureException;
import org.sgrewritten.stargate.exception.NoFormatFoundException;
import org.sgrewritten.stargate.exception.TranslatableException;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.exception.ParseException;
import org.sgrewritten.stargatemechanics.locale.LocalizedMessageFormatter;
import org.sgrewritten.stargatemechanics.locale.LocalizedMessageType;
import org.sgrewritten.stargatemechanics.locale.MechanicsLanguageManager;
import org.sgrewritten.stargatemechanics.locale.MessageSender;
import org.sgrewritten.stargatemechanics.metadata.MetaData;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.portal.behavior.BehaviorInserter;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;
import org.sgrewritten.stargatemechanics.signcoloring.ColorOverride;
import org.sgrewritten.stargatemechanics.signcoloring.ColorOverrideFormatter;
import org.sgrewritten.stargatemechanics.signcoloring.ColoringOverrideRegistry;
import org.sgrewritten.stargatemechanics.utils.ButtonUtils;
import org.sgrewritten.stargatemechanics.utils.CoordinateParser;
import org.sgrewritten.stargatemechanics.utils.SignUtils;
import org.sgrewritten.stargatemechanics.utils.TimeParser;
import org.sgrewritten.stargatemechanics.utils.redstone.RedstoneUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StargateEventListener implements Listener {

    private final StargateAPI stargateAPI;
    private final RedstoneEngine engine;
    private final ColoringOverrideRegistry coloringOverrideRegistry;
    private final MechanicsLanguageManager mechanicsLanguageManager;
    private StargateMechanics plugin;

    public StargateEventListener(StargateMechanics plugin, StargateAPI stargateAPI, RedstoneEngine engine, ColoringOverrideRegistry coloringOverrideRegistry, MechanicsLanguageManager mechanicsLanguageManager) {
        this.plugin = plugin;
        this.stargateAPI = stargateAPI;
        this.engine = engine;
        this.coloringOverrideRegistry = coloringOverrideRegistry;
        this.mechanicsLanguageManager = mechanicsLanguageManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onStargateCreate(StargateCreatePortalEvent event) {
        if (!(event.getPortal() instanceof RealPortal realPortal)) {
            return;
        }
        if (event.getPortal().hasFlag(PortalFlag.NO_SIGN)) {
            SignUtils.removeSignsFromPortal(realPortal);
        }
        if (event.getPortal().hasFlag(MechanicsFlag.REDSTONE_POWERED)) {
            ButtonUtils.removeButtonsFromPortal(realPortal);
            RedstoneUtils.loadPortal(realPortal, engine);
            if (event.getPortal().hasFlag(PortalFlag.NETWORKED) || event.getPortal().hasFlag(PortalFlag.ALWAYS_ON)) {
                event.removeFlag(MechanicsFlag.REDSTONE_POWERED);
                String unformattedMsg = mechanicsLanguageManager.getLocalizedMsg(LocalizedMessageType.FLAG_REMOVED_INCOMPATIBLE);
                MessageSender.sendMessage(event.getEntity(), LocalizedMessageFormatter.insertFlags(unformattedMsg, List.of(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation())));
            }
        }
        if (realPortal.hasFlag(MechanicsFlag.GENERATE)) {
            insertCoordData(realPortal, event.getLine(3), event.getEntity(), MechanicsFlag.GENERATE);
        }
        BehaviorInserter.insertMechanicsBehavior(realPortal, stargateAPI, plugin, mechanicsLanguageManager);
        if (event.getPortal().hasFlag(MechanicsFlag.OPEN_TIMER)) {
            insertTimerData(realPortal, event.getLine(3), event.getEntity(), MechanicsFlag.OPEN_TIMER);
        }
        if (event.getPortal().hasFlag(MechanicsFlag.DESTROY_TIMER)) {
            insertTimerData(realPortal, event.getLine(3), event.getEntity(), MechanicsFlag.DESTROY_TIMER);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onStargatePreCreatePortal(StargatePreCreatePortalEvent event) {
        if (event.getFlagString().trim().equals("{}")) {
            try {
                event.getGateBuilder().build();
            } catch (NoFormatFoundException | GateConflictException | InvalidStructureException e) {
                return;
            }
            event.setCancelled(true);
            createPortal(event.getPlayer(), flagArgs -> new BukkitRunnable() {
                @Override
                public void run() {
                    PortalBuilder builder = event.getPortalBuilder();
                    builder.setFlags(flagArgs);
                    try {
                        builder.build();
                    } catch (TranslatableException | GateConflictException | NoFormatFoundException |
                             InvalidStructureException e) {
                        e.printStackTrace();
                    }
                }
            }.runTask(plugin));
        }
    }

    private void createPortal(Player activator, Consumer<String> action) {
        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        ItemStack itemLeft = new ItemStack(Material.BARRIER);
        ItemMeta itemMetaLeft = itemLeft.getItemMeta();
        itemMetaLeft.displayName(Component.text("Cancel").color(NamedTextColor.RED));
        itemLeft.setItemMeta(itemMetaLeft);
        ItemStack itemOutput = new ItemStack(Material.GREEN_WOOL);
        ItemMeta itemMetaOutput = itemOutput.getItemMeta();
        itemMetaOutput.displayName(Component.text("Accept").color(NamedTextColor.GREEN));
        itemOutput.setItemMeta(itemMetaOutput);
        builder.itemLeft(itemLeft).itemOutput(itemOutput).title("Type flag arguments").plugin(plugin).text("");
        builder.onClickAsync((slot, stateSnapshot) -> CompletableFuture.supplyAsync(() -> {
            if (slot == AnvilGUI.Slot.INPUT_RIGHT) {
                return Collections.emptyList();
            }
            if (slot == AnvilGUI.Slot.OUTPUT) {
                action.accept(stateSnapshot.getText());
            }
            return List.of(AnvilGUI.ResponseAction.close());
        }));
        builder.interactableSlots(AnvilGUI.Slot.INPUT_LEFT, AnvilGUI.Slot.INPUT_RIGHT, AnvilGUI.Slot.OUTPUT);
        builder.open(activator);
    }

    private void insertCoordData(RealPortal portal, String line, Entity entity, PortalFlag flag) {
        String value = findFlagInstructionsString(line, flag);
        if (flag != MechanicsFlag.GENERATE) {
            throw new UnsupportedOperationException();
        }
        try {
            CoordinateParser.getLocationFromPortal(portal);
            insertMetaDataFromFlagArgument(portal, MetaData.DESTINATION_COORDS, value);
        } catch (ParseException e) {
            MessageSender.sendMessage(entity, mechanicsLanguageManager.getLocalizedMsg(LocalizedMessageType.FLAG_REMOVED_INVALID_ARGUMENT));
            portal.removeFlag(flag);
        }
    }

    private void insertTimerData(RealPortal portal, String line, Entity entity, PortalFlag flag) {
        String value = findFlagInstructionsString(line, flag);
        try {
            TimeParser.parseTime(value);
            if (MechanicsFlag.DESTROY_TIMER == flag) {
                insertMetaDataFromFlagArgument(portal, MetaData.DESTROY_COUNTDOWN, value);
            } else if (MechanicsFlag.OPEN_TIMER == flag) {
                insertMetaDataFromFlagArgument(portal, MetaData.OPEN_COUNTDOWN, value);
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (ParseException | IllegalStateException e) {
            MessageSender.sendMessage(entity, mechanicsLanguageManager.getLocalizedMsg(LocalizedMessageType.FLAG_REMOVED_INVALID_ARGUMENT));
            portal.removeFlag(flag);
        }
    }

    private void insertMetaDataFromFlagArgument(RealPortal realPortal, MetaData metaDataType, String value) {
        realPortal.setMetadata(new JsonPrimitive(value), metaDataType.toString());
    }


    private String findFlagInstructionsString(String line, PortalFlag flag) {
        Pattern pattern = Pattern.compile("(?<=" + flag.getCharacterRepresentation() + "\\{)(.*?)(?=\\})");
        Matcher matcher = pattern.matcher(line.toUpperCase());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    @EventHandler(ignoreCancelled = true)
    public void onStargateClosePortalEvent(StargateClosePortalEvent event) {
        if (event.getPortal().hasFlag(MechanicsFlag.REDSTONE_POWERED) && !event.getForce()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onStargateSignDyeChangePortalEvent(StargateSignDyeChangePortalEvent event) {
        if (!(event.getPortal() instanceof RealPortal)) {
            return;
        }
        coloringOverrideRegistry.registerOverride(event.getLocation(), new ColorOverride(event.getColorChange()));
        PortalPosition portalPosition = event.getPortalPosition();
        portalPosition.setMetadata(new JsonPrimitive(event.getColorChange().name()), MetaData.SIGN_COLOR.name());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStargateSignFormatGateEvent(StargateSignFormatPortalEvent event) {
        ColorOverride colorOverride = coloringOverrideRegistry.getColorOverride(event.getSign().getLocation());
        RealPortal portal = (RealPortal) event.getPortal();
        for (SignLine line : event.getLines()) {
            ColorOverrideFormatter.formatFromOverride(colorOverride, line);
            if (line.getType() == SignLineType.NETWORK && portal.hasFlag(PortalFlag.HIDE_NETWORK)) {
                line.getComponents().clear();
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
        return player != null && (player.hasPermission(BypassPermission.HIDDEN.getPermissionString()) || portalToSee.getOwnerUUID().equals(player.getUniqueId()));
    }

    @EventHandler
    public void onStargateSendMessagePortalEvent(SyncStargateSendMessagePortalEvent event) {
        if (shouldCancelStargateSendMessagePortalEvent(event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onStargateSendMessagePortalEvent(AsyncStargateSendMessagePortalEvent event) {
        if (shouldCancelStargateSendMessagePortalEvent(event)) {
            event.setCancelled(true);
        }
    }

    private boolean shouldCancelStargateSendMessagePortalEvent(StargateSendMessagePortalEvent event) {
        return event.getPortal().hasFlag(PortalFlag.SILENT);
    }

}
