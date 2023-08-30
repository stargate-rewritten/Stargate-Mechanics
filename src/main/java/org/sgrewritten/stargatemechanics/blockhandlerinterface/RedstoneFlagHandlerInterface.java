package org.sgrewritten.stargatemechanics.blockhandlerinterface;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargate.api.BlockHandlerInterface;
import org.sgrewritten.stargate.api.network.portal.PositionType;
import org.sgrewritten.stargate.api.Priority;
import org.sgrewritten.stargate.api.network.portal.MetaData;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;

public class RedstoneFlagHandlerInterface implements BlockHandlerInterface {

    private final StargateMechanics plugin;

    public RedstoneFlagHandlerInterface(StargateMechanics plugin){
        this.plugin = plugin;
    }
    @Override
    public @NotNull PositionType getInterfaceType() {
        return PositionType.BUTTON;
    }

    @Override
    public @NotNull Material getHandledMaterial() {
        return Material.TARGET;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Priority getPriority() {
        return Priority.NORMAL;
    }

    @Override
    public @Nullable Character getFlag() {
        return MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation();
    }

    @Override
    public boolean registerBlock(Location location, @Nullable Player player, Portal portal, MetaData metaData) {
        return false;
    }

    @Override
    public void unRegisterBlock(Location location, Portal portal) {

    }
}
