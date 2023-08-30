package org.sgrewritten.stargatemechanics.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.PositionType;
import org.sgrewritten.stargate.api.network.portal.RealPortal;

import java.util.ArrayList;
import java.util.List;

public class NoSignUtils {
    public static void removeSignsFromPortal(RealPortal portal) {
        List<PortalPosition> positionsToRemove = new ArrayList<>();
        GateAPI gate = portal.getGate();
        for(PortalPosition portalPosition : gate.getPortalPositions()) {
            if(portalPosition.getPluginName().equals("Stargate") && portalPosition.getPositionType() == PositionType.SIGN){
                positionsToRemove.add(portalPosition);
            }
        }
        for(PortalPosition portalPosition : positionsToRemove) {
            Location location = gate.getLocation(portalPosition.getRelativePositionLocation());
            gate.removePortalPosition(location);
            location.getBlock().setType(Material.AIR);
        }
    }

}
