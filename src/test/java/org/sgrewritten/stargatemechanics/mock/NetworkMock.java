package org.sgrewritten.stargatemechanics.mock;

import org.bukkit.entity.Player;
import org.sgrewritten.stargate.api.network.Network;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.exception.UnimplementedFlagException;
import org.sgrewritten.stargate.exception.name.InvalidNameException;
import org.sgrewritten.stargate.exception.name.NameConflictException;
import org.sgrewritten.stargate.exception.name.NameLengthException;
import org.sgrewritten.stargate.network.NetworkType;
import org.sgrewritten.stargate.network.StorageType;
import org.sgrewritten.stargate.network.portal.formatting.HighlightingStyle;

import java.util.Collection;
import java.util.Set;

public class NetworkMock implements Network {

    private final String name;

    public NetworkMock(String name){
        this.name = name;
    }
    @Override
    public Collection<Portal> getAllPortals() {
        return null;
    }

    @Override
    public Portal getPortal(String s) {
        return null;
    }

    @Override
    public void removePortal(Portal portal, boolean b) {

    }

    @Override
    public void addPortal(Portal portal, boolean b) throws NameConflictException {

    }

    @Override
    public boolean isPortalNameTaken(String s) {
        return false;
    }

    @Override
    public void updatePortals() {

    }

    @Override
    public Set<String> getAvailablePortals(Player player, Portal portal) {
        return null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void assignToRegistry(RegistryAPI registryAPI) {

    }

    @Override
    public HighlightingStyle getHighlightingStyle() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public NetworkType getType() {
        return null;
    }

    @Override
    public StorageType getStorageType() {
        return null;
    }

    @Override
    public void setID(String s) throws InvalidNameException, NameLengthException, UnimplementedFlagException {

    }
}
