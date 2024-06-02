package org.sgrewritten.stargatemechanics.portal.behavior;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.gate.GateFormatAPI;
import org.sgrewritten.stargate.api.gate.GateFormatRegistry;
import org.sgrewritten.stargate.api.network.Network;
import org.sgrewritten.stargate.api.network.PortalBuilder;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.network.portal.behavior.AbstractPortalBehavior;
import org.sgrewritten.stargate.api.network.portal.flag.PortalFlag;
import org.sgrewritten.stargate.api.network.portal.formatting.SignLineType;
import org.sgrewritten.stargate.api.network.portal.formatting.data.LineData;
import org.sgrewritten.stargate.api.network.portal.formatting.data.NetworkLineData;
import org.sgrewritten.stargate.api.network.portal.formatting.data.PortalLineData;
import org.sgrewritten.stargate.api.network.portal.formatting.data.TextLineData;
import org.sgrewritten.stargate.exception.GateConflictException;
import org.sgrewritten.stargate.exception.InvalidStructureException;
import org.sgrewritten.stargate.exception.NoFormatFoundException;
import org.sgrewritten.stargate.exception.TranslatableException;
import org.sgrewritten.stargatemechanics.ConfigurationOption;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.exception.GateGenerationFault;
import org.sgrewritten.stargatemechanics.exception.ParseException;
import org.sgrewritten.stargatemechanics.generation.PortalPositionFinder;
import org.sgrewritten.stargatemechanics.locale.LocalizedMessageType;
import org.sgrewritten.stargatemechanics.locale.MechanicsLanguageManager;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.utils.CoordinateParser;

import java.util.Optional;

public class GenerateBehavior extends AbstractPortalBehavior {
    private final boolean randomCoordinate;
    private final boolean generateName;
    private final String generatedPortalFlagString;
    private String destinationString;
    private final MechanicsLanguageManager mechanicsLanguageManager;
    private final StargateAPI stargateAPI;
    private final StargateMechanics plugin;
    private static final int MAX_DEST_WIDTH = 12;
    private static final String DESTINATION = "destination";

    public GenerateBehavior(StargateAPI stargateAPI, StargateMechanics plugin, MechanicsLanguageManager mechanicsLanguageManager, @Nullable String destinationString, boolean isRandomDestination, String generatedPortalFlagString) {
        super(stargateAPI.getLanguageManager());
        this.destinationString = destinationString;
        this.mechanicsLanguageManager = mechanicsLanguageManager;
        this.stargateAPI = stargateAPI;
        this.plugin = plugin;
        this.randomCoordinate = isRandomDestination;
        this.generateName = destinationString == null;
        this.generatedPortalFlagString = generatedPortalFlagString;
    }

    @Override
    public void update() {
        // I don't think anything needs to be updated at the moment for this type of portal behavior
    }

    @Override
    public @Nullable Portal getDestination() {
        return super.portal.getNetwork().getPortal(destinationString);
    }

    @Override
    public @NotNull LineData @NotNull [] getLines() {
        if (getDestination() == null) {
            return getInactiveGateLines();
        }
        return new LineData[]{
                new PortalLineData(super.portal, SignLineType.THIS_PORTAL),
                new PortalLineData(getDestination(), SignLineType.DESTINATION_PORTAL),
                new NetworkLineData(portal.getNetwork()),
                new TextLineData()
        };
    }

    private LineData[] getInactiveGateLines() {
        String line2;
        if (super.portal.hasFlag(PortalFlag.ALWAYS_ON)) {
            line2 = mechanicsLanguageManager.getLocalizedString(LocalizedMessageType.ENTER_GATE_TO);
        } else {
            line2 = mechanicsLanguageManager.getLocalizedString(LocalizedMessageType.CLICK_BUTTON_TO);
        }
        return new LineData[]{
                new PortalLineData(super.portal, SignLineType.THIS_PORTAL),
                new TextLineData(line2, SignLineType.TEXT),
                new TextLineData(mechanicsLanguageManager.getLocalizedString(LocalizedMessageType.CREATE_PORTAL), SignLineType.TEXT),
                new NetworkLineData(super.portal.getNetwork())
        };
    }

    @Override
    public void onButtonClick(PlayerInteractEvent event) {
        if (getDestination() == null) {
            if (getDestination() != null && getDestination() instanceof RealPortal destinationPortal && !generateName) {
                stargateAPI.getNetworkManager().destroyPortal(destinationPortal);
            }
            createGateFromFlagArguments(super.portal, event.getPlayer());
        }
        super.onButtonClick(event);
    }

    public void onEnter() {
        if (randomCoordinate && generateName) {
            destinationString = null;
            portal.redrawSigns();
        }
    }

    @Override
    public @NotNull PortalFlag getAttachedFlag() {
        return MechanicsFlag.GENERATE;
    }

    @Override
    public @Nullable String getDestinationName() {
        return destinationString;
    }

    @Override
    public void assignPortal(@NotNull RealPortal portal) {
        super.assignPortal(portal);
        if (destinationString == null) {
            JsonElement destinationData = portal.getMetadata(DESTINATION);
            if (destinationData != null) {
                this.destinationString = destinationData.getAsString();
            }
        }
    }

    private void createGateFromFlagArguments(RealPortal realPortal, @NotNull Player player) {
        try {
            Location destinationCoordinate = CoordinateParser.getLocationFromPortal(realPortal);
            if (destinationCoordinate != null) {
                Optional<String> gateFormatName = GateFormatRegistry.getAllGateFormatNames().stream().findAny();
                if (gateFormatName.isPresent()) {
                    GateFormatAPI gateFormatAPI = GateFormatRegistry.getFormat(gateFormatName.get());
                    PortalPositionFinder portalPositionFinder = new PortalPositionFinder(destinationCoordinate, gateFormatAPI, stargateAPI.getRegistry());
                    GateAPI gateAPi = portalPositionFinder.getValidGate(4);
                    gateAPi.forceGenerateStructure();
                    gateAPi.calculatePortalPositions(false);
                    String flagsString = plugin.getConfig().getString(ConfigurationOption.RANDOM_GENERATED_GATE_FLAG_STRING.getKey());
                    String destinationName = getDestinationName(realPortal, destinationCoordinate);
                    if (destinationName == null) {
                        return;
                    }
                    PortalBuilder portalBuilder = new PortalBuilder(stargateAPI, Bukkit.getOfflinePlayer(realPortal.getOwnerUUID()), destinationName).setGate(gateAPi);
                    if (flagsString != null) {
                        portalBuilder.setFlags(flagsString);
                    }
                    portalBuilder.setDestination(realPortal.getId());
                    portalBuilder.setNetwork(realPortal.getNetwork());
                    if (generatedPortalFlagString != null) {
                        portalBuilder.setFlags(generatedPortalFlagString);
                    }
                    portalBuilder.addEventHandling(player).build();
                    if (generateName) {
                        realPortal.setMetadata(new JsonPrimitive(destinationName), DESTINATION);
                        this.destinationString = destinationName;
                        portal.redrawSigns();
                    }
                }
            }
        } catch (ParseException | GateGenerationFault | InvalidStructureException | TranslatableException |
                 GateConflictException | NoFormatFoundException e) {
            e.printStackTrace();
        }
    }

    private @Nullable String getDestinationName(@NotNull RealPortal realPortal, @NotNull Location destinationLocation) {
        if (generateName) {
            return getDestinationNameFromBiome(destinationLocation.getBlock().getComputedBiome(), realPortal.getNetwork());
        } else {
            return getDestinationName();
        }
    }

    private String getDestinationNameFromBiome(Biome biome, Network network) {
        String biomeName = biome.name().toLowerCase();
        int counter = 0;
        String destinationName;
        do {
            String counterString = String.valueOf(counter);
            int biomeLength = MAX_DEST_WIDTH - counterString.length();
            destinationName = (biomeName.length() > biomeLength ? biomeName.substring(0, biomeLength) : biomeName) + counterString;
            counter++;
        } while (network.isPortalNameTaken(destinationName));
        return destinationName;
    }
}
