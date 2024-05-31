package org.sgrewritten.stargatemechanics.mock;

import org.bukkit.DyeColor;
import org.bukkit.Location;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PortalMock implements RealPortal {

    private final GateAPI gate;
    private final Set<PortalFlag> flags;
    private final Location exit;
    private UUID ownerUUID;
    private Network network;
    private String metaData;
    private PortalBehavior behavior;

    public PortalMock(GateAPI gate, Network network, Location exit) {
        this(gate, network, exit, new HashSet<>());
    }

    public PortalMock(GateAPI gate, Network network, Location exit, Set<PortalFlag> flags) {
        this(gate, network, exit, flags, UUID.randomUUID());
    }

    public PortalMock(GateAPI gate, Network network, Location exit, Set<PortalFlag> flags, UUID ownerUUID) {
        this.gate = gate;
        this.flags = flags;
        this.network = network;
        this.ownerUUID = ownerUUID;
        this.exit = exit;
        this.behavior = new MockBehavior();
    }

    @Override
    public void open(@Nullable Portal portal, @Nullable Player player) {

    }

    @Override
    public void setSignColor(DyeColor dyeColor, PortalPosition portalPosition) {

    }

    @Override
    public GateAPI getGate() {
        return this.gate;
    }

    @Override
    public void close(long l) {

    }

    @Override
    public Location getExit() {
        return exit;
    }

    @Override
    public List<Location> getPortalPosition(PositionType positionType) {
        return null;
    }

    @Override
    public UUID getActivatorUUID() {
        return null;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public BlockFace getExitFacing() {
        return null;
    }

    @Override
    public PortalBehavior getBehavior() {
        return this.behavior;
    }

    @Override
    public void setBehavior(PortalBehavior portalBehavior) {
        this.behavior = portalBehavior;
    }

    @Override
    public void redrawSigns() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isOpenFor(Entity entity) {
        return false;
    }

    @Override
    public void teleportHere(Entity entity, RealPortal realPortal) {

    }

    @Override
    public void doTeleport(Entity entity) {

    }

    @Override
    public void close(boolean b) {

    }

    @Override
    public void open(Player player) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void overrideDestination(Portal portal) {

    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public void setNetwork(Network network) throws NameConflictException {
        this.network = network;
    }

    @Override
    public void setOwner(UUID uuid) {
        this.ownerUUID = uuid;
    }

    @Override
    public boolean hasFlag(PortalFlag portalFlag) {
        return flags.contains(portalFlag);
    }


    @Override
    public void addFlag(PortalFlag flag) throws UnsupportedOperationException {
        flags.add(flag);
    }

    @Override
    public void removeFlag(PortalFlag flag) throws UnsupportedOperationException {
        flags.remove(flag);
    }

    @Override
    public String getAllFlagsString() {
        return null;
    }

    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void updateState() {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public GlobalPortalId getGlobalId() {
        return null;
    }

    @Override
    public StorageType getStorageType() {
        return null;
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public void activate(Player player) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void doTeleport(@NotNull Entity entity, @Nullable Portal portal) {

    }

    @Override
    public boolean isDestroyed() {
        return false;
    }

    @Override
    public void setMetadata(@Nullable String s) {
        this.metaData = s;
    }

    @Override
    public @Nullable String getMetadata() {
        return this.metaData;
    }
}
