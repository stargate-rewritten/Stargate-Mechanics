package org.sgrewritten.stargatemechanics.portal;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.network.Network;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.PositionType;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.network.portal.behavior.PortalBehavior;
import org.sgrewritten.stargate.api.network.portal.flag.PortalFlag;
import org.sgrewritten.stargate.exception.name.NameConflictException;
import org.sgrewritten.stargate.network.StorageType;
import org.sgrewritten.stargate.network.portal.GlobalPortalId;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class PortalWrapper implements RealPortal {

    private final Function<OfflinePlayer, RealPortal> constructor;
    private final UUID defaultOwner;
    private RealPortal portal = null;
    private UUID playerUuid = null;

    public PortalWrapper(Function<OfflinePlayer, RealPortal> constructor, UUID defaultOwner) {
        this.constructor = constructor;
        this.defaultOwner = defaultOwner;
    }


    private void setPortalIfNotPresent() {
        if (this.portal != null) {
            return;
        }
        if (playerUuid == null) {
            throw new IllegalStateException("Portal wrapper requires player target to function");
        }
        this.portal = constructor.apply(findPlayer());
        if (this.portal == null) {
            throw new IllegalStateException("Unexpected use of portal wrapper");
        }
    }

    private void setEventHandling(UUID owner) {
        playerUuid = owner;
    }

    private OfflinePlayer findPlayer() {
        if(playerUuid == null){
            playerUuid = defaultOwner;
        }
        OfflinePlayer player = Bukkit.getPlayer(playerUuid);
        if(player == null){
            player = Bukkit.getOfflinePlayer(playerUuid);
        }
        return player;
    }

    @Override
    public void open(@Nullable Portal portal, @Nullable Player player) {
        if(player != null) {
            setEventHandling(player.getUniqueId());
        }
        setPortalIfNotPresent();
        this.portal.open(portal, player);
    }

    @Override
    public void setSignColor(DyeColor dyeColor, PortalPosition portalPosition) {
        setPortalIfNotPresent();
        this.portal.setSignColor(dyeColor, portalPosition);
    }

    @Override
    public GateAPI getGate() {
        setPortalIfNotPresent();
        return this.portal.getGate();
    }

    @Override
    public void close(long l) {
        setPortalIfNotPresent();
        this.portal.close(l);
    }

    @Override
    public Location getExit() {
        setPortalIfNotPresent();
        return this.portal.getExit();
    }

    @Override
    public List<Location> getPortalPosition(PositionType positionType) {
        setPortalIfNotPresent();
        return this.portal.getPortalPosition(positionType);
    }

    @Override
    public UUID getActivatorUUID() {
        setPortalIfNotPresent();
        return this.portal.getActivatorUUID();
    }

    @Override
    public void deactivate() {
        setPortalIfNotPresent();
        this.portal.deactivate();
    }

    @Override
    public BlockFace getExitFacing() {
        setPortalIfNotPresent();
        return this.portal.getExitFacing();
    }

    @Override
    public PortalBehavior getBehavior() {
        setPortalIfNotPresent();
        return this.portal.getBehavior();
    }

    @Override
    public void setBehavior(PortalBehavior portalBehavior) {
        setPortalIfNotPresent();
        this.portal.setBehavior(portalBehavior);
    }

    @Override
    public void redrawSigns() {
        setPortalIfNotPresent();
        this.portal.redrawSigns();
    }

    @Override
    public void activate(Player player) {
        this.playerUuid = player.getUniqueId();
        setPortalIfNotPresent();
        this.portal.activate(player);
    }

    @Override
    public boolean isActive() {
        setPortalIfNotPresent();
        return this.portal.isActive();
    }

    @Override
    public void doTeleport(@NotNull Entity entity, @Nullable Portal portal) {
        setEventHandling(entity.getUniqueId());
        setPortalIfNotPresent();
        this.portal.doTeleport(entity, portal);
    }

    @Override
    public void doTeleport(@NotNull Entity entity) {
        setPortalIfNotPresent();
        this.portal.doTeleport(entity);
    }

    @Override
    public void setMetadata(@Nullable String s) {
        setPortalIfNotPresent();
        this.portal.setMetadata(s);
    }

    @Override
    public @Nullable String getMetadata() {
        setPortalIfNotPresent();
        return this.portal.getMetadata();
    }

    @Override
    public void destroy() {
        setPortalIfNotPresent();
        this.portal.destroy();
    }

    @Override
    public boolean isOpen() {
        setPortalIfNotPresent();
        return this.portal.isOpen();
    }

    @Override
    public boolean isOpenFor(Entity entity) {
        setPortalIfNotPresent();
        return this.portal.isOpenFor(entity);
    }

    @Override
    public void teleportHere(Entity entity, RealPortal realPortal) {
        setEventHandling(entity.getUniqueId());
        setPortalIfNotPresent();
        this.portal.teleportHere(entity, realPortal);
    }

    @Override
    public void close(boolean b) {
        setPortalIfNotPresent();
        this.portal.close(b);
    }

    @Override
    public void open(Player player) {
        setEventHandling(player.getUniqueId());
        setPortalIfNotPresent();
        this.portal.open(player);
    }

    @Override
    public String getName() {
        setPortalIfNotPresent();
        return this.portal.getName();
    }

    @Override
    public void overrideDestination(Portal portal) {
        setPortalIfNotPresent();
        this.portal.overrideDestination(portal);
    }

    @Override
    public Network getNetwork() {
        setPortalIfNotPresent();
        return this.portal.getNetwork();
    }

    @Override
    public void setNetwork(Network network) throws NameConflictException {
        setPortalIfNotPresent();
        this.portal.setNetwork(network);
    }

    @Override
    public void setOwner(UUID uuid) {
        setPortalIfNotPresent();
        this.portal.setOwner(uuid);
    }

    @Override
    public boolean hasFlag(PortalFlag portalFlag) {
        setPortalIfNotPresent();
        return this.portal.hasFlag(portalFlag);
    }

    @Override
    public void removeFlag(PortalFlag portalFlag) {
        setPortalIfNotPresent();
        this.portal.removeFlag(portalFlag);
    }

    @Override
    public void addFlag(PortalFlag portalFlag) {
        setPortalIfNotPresent();
        this.portal.addFlag(portalFlag);
    }

    @Override
    public String getAllFlagsString() {
        setPortalIfNotPresent();
        return this.portal.getAllFlagsString();
    }

    @Override
    public UUID getOwnerUUID() {
        setPortalIfNotPresent();
        return this.portal.getOwnerUUID();
    }

    @Override
    public void updateState() {
        setPortalIfNotPresent();
        this.portal.updateState();
    }

    @Override
    public String getId() {
        setPortalIfNotPresent();
        return this.portal.getId();
    }

    @Override
    public GlobalPortalId getGlobalId() {
        setPortalIfNotPresent();
        return this.portal.getGlobalId();
    }

    @Override
    public StorageType getStorageType() {
        setPortalIfNotPresent();
        return this.portal.getStorageType();
    }

    @Override
    public void setName(String s) {
        setPortalIfNotPresent();
        this.portal.setName(s);
    }

    @Override
    public boolean isDestroyed() {
        setPortalIfNotPresent();
        return this.portal.isDestroyed();
    }
}
