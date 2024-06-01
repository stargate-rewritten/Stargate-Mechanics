package org.sgrewritten.stargatemechanics.utils;

import org.sgrewritten.stargate.api.network.Network;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.network.StorageType;

import java.util.stream.Stream;

public class PortalUtil {

    public static Stream<RealPortal> getAllPortals(RegistryAPI registryAPI) {
        Stream<Network> localStream = registryAPI.getNetworkRegistry(StorageType.LOCAL).stream();
        Stream<Network> interserverStream = registryAPI.getNetworkRegistry(StorageType.INTER_SERVER).stream();
        Stream<Network> allNetworks = Stream.concat(localStream, interserverStream);
        Stream.Builder<Portal> builder = Stream.builder();
        allNetworks.forEach(network -> network.getAllPortals().forEach(builder::add));
        return builder.build().filter(portal -> portal instanceof RealPortal).map(portal -> (RealPortal) portal);
    }
}
