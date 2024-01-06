package org.sgrewritten.stargatemechanics.mock;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargate.api.gate.GateStructureType;
import org.sgrewritten.stargate.api.network.Network;
import org.sgrewritten.stargate.api.network.NetworkRegistry;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.*;
import org.sgrewritten.stargate.exception.UnimplementedFlagException;
import org.sgrewritten.stargate.exception.name.InvalidNameException;
import org.sgrewritten.stargate.exception.name.NameConflictException;
import org.sgrewritten.stargate.exception.name.NameLengthException;
import org.sgrewritten.stargate.network.NetworkType;
import org.sgrewritten.stargate.network.StorageType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegistryMock implements RegistryAPI {

    @Override
    public void unregisterPortal(Portal portal) {

    }

    @Override
    public void updateAllPortals() {

    }

    @Override
    public RealPortal getPortal(BlockLocation blockLocation, GateStructureType gateStructureType) {
        return null;
    }

    @Override
    public RealPortal getPortal(BlockLocation blockLocation, GateStructureType[] gateStructureTypes) {
        return null;
    }

    @Override
    public RealPortal getPortal(Location location, GateStructureType gateStructureType) {
        return null;
    }

    @Override
    public RealPortal getPortal(Location location, GateStructureType[] gateStructureTypes) {
        return null;
    }

    @Override
    public RealPortal getPortal(Location location) {
        return null;
    }

    @Override
    public boolean isPartOfPortal(List<Block> list) {
        return false;
    }

    @Override
    public boolean isNextToPortal(Location location, GateStructureType gateStructureType) {
        return false;
    }

    @Override
    public List<RealPortal> getPortalsFromTouchingBlock(Location location, GateStructureType gateStructureType) {
        return null;
    }

    @Override
    public void registerLocations(GateStructureType gateStructureType, Map<BlockLocation, RealPortal> map) {

    }

    @Override
    public void registerLocation(GateStructureType gateStructureType, BlockLocation blockLocation, RealPortal realPortal) {

    }

    @Override
    public void unRegisterLocation(GateStructureType gateStructureType, BlockLocation blockLocation) {

    }

    @Override
    public void registerPortal(RealPortal realPortal) {

    }

    @Override
    public boolean networkExists(String s, StorageType storageType) {
        return false;
    }

    @Override
    public NetworkRegistry getNetworkRegistry(StorageType storageType) {
        return null;
    }

    @Override
    public @Nullable Network getNetwork(String s, StorageType storageType) {
        return null;
    }

    @Override
    public String getValidNewName(Network network) throws InvalidNameException {
        return null;
    }

    @Override
    public Map<BlockLocation, PortalPosition> getPortalPositions() {
        return null;
    }

    @Override
    public Map<BlockLocation, PortalPosition> getPortalPositionsOwnedByPlugin(Plugin plugin) {
        return null;
    }

    @Override
    public PortalPosition savePortalPosition(RealPortal realPortal, Location location, PositionType positionType, Plugin plugin) {
        return null;
    }

    @Override
    public void removePortalPosition(Location location) {

    }

    @Override
    public void registerPortalPosition(PortalPosition portalPosition, Location location, RealPortal realPortal) {

    }

    @Override
    public @Nullable PortalPosition getPortalPosition(Location location) {
        return null;
    }

    @Override
    public @Nullable RealPortal getPortalFromPortalPosition(PortalPosition portalPosition) {
        return null;
    }

    @Override
    public void registerNetwork(Network network) {

    }

    @Override
    public void renameNetwork(String s, String s1, StorageType storageType) throws InvalidNameException, UnimplementedFlagException, NameLengthException {

    }
}
