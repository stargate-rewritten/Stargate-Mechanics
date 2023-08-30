package org.sgrewritten.stargatemechanics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;

public class BlockEventListener implements Listener{
    private final RegistryAPI registry;
    public BlockEventListener(RegistryAPI registry){
        this.registry = registry;
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        // Filter away any changes in current that only modifies the signal strength
        if( (event.getNewCurrent() == 0) ^ (event.getOldCurrent() == 0) ){
            return;
        }
        // TODO Implement settings
        if(!false) {
            return;
        }
        PortalPosition portalPosition = registry.getPortalPosition(event.getBlock().getLocation());
        RealPortal portal = registry.getPortalFromPortalPosition(portalPosition);
        if(portal.getDestination() == null || !portal.hasFlag(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation())) {
            return;
        }
        if(event.getNewCurrent() == 0) {
            portal.close(false);
        } else {
            portal.open(null);
        }
    }
}
