package org.sgrewritten.stargatemechanics.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockVector;
import org.sgrewritten.stargate.api.network.portal.*;
import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.gate.GateStructureType;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.signcoloring.ColoringOverrideRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class PlayerEventListener implements Listener {

    private final StargateAPI stargateAPI;
    private final Map<RealPortal,Long> removeSignMap = new HashMap<>();
    private final Map<BlockLocation,BlockState> previousData = new HashMap<>();
    private final StargateMechanics plugin;

    private final static int DESTROY_SIGN_DELAY = 30; // seconds
    private final ColoringOverrideRegistry registry;

    public PlayerEventListener(StargateAPI stargateAPI, ColoringOverrideRegistry registry, StargateMechanics plugin){
        this.stargateAPI = stargateAPI;
        this.plugin = plugin;
        this.registry = registry;
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerInteractEvent(PlayerInteractEvent event){
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null){
            return;
        }
        if(event.getAction().isRightClick()){
            Location location = clickedBlock.getLocation();
            RealPortal portal = stargateAPI.getRegistry().getPortal(location, GateStructureType.FRAME);
            if(portal != null && portal.hasFlag(PortalFlag.NO_SIGN)) {
                createSign(event.getPlayer(), portal, location);
            }
        }
        dealWithGeneralInteraction(event.getPlayer(),clickedBlock.getLocation());
    }

    private void dealWithGeneralInteraction(Player player, Location location) {
        RealPortal portal = stargateAPI.getRegistry().getPortal(location);
        if(portal == null){
            return;
        }
        if(portal.hasFlag(PortalFlag.NO_SIGN)){
            long timePoint = System.currentTimeMillis();
            removeSignMap.put(portal,timePoint);
            Bukkit.getScheduler().runTaskLater(plugin,() -> {
                Long latestTimePoint = removeSignMap.get(portal);
                if(latestTimePoint != null && latestTimePoint == timePoint){
                    removeSignsFromPortal(portal);
                }
                }, 20*DESTROY_SIGN_DELAY
            );
        }
    }

    private void removeSignsFromPortal(RealPortal portal) {
        GateAPI gate = portal.getGate();
        for(PortalPosition portalPosition : gate.getPortalPositions()){
            if(portalPosition.getPositionType() == PositionType.SIGN && portalPosition.getPluginName().equals("Stargate")){
                Location location = gate.getLocation(portalPosition.getRelativePositionLocation());
                BlockState previousState = previousData.get(new BlockLocation(location));
                if(previousState == null){
                    continue;
                }
                stargateAPI.getRegistry().removePortalPosition(location);
                previousState.update(true);
            }
        }
        removeSignMap.remove(portal);
    }

    private void createSign(Player player, RealPortal portal, Location clickedBlockLocation) {
        if(!stargateAPI.getPermissionManager(player).hasAccessPermission(portal) || removeSignMap.containsKey(portal)){
            return;
        }

        double closestBlockFromClickedBlockDistance = -1;
        Location closestLocation = null;
        BlockVector closestRelativeLocation = null;
        GateAPI gate = portal.getGate();
        for(BlockVector controlBlock : gate.getFormat().getControlBlocks()){
            Location location = gate.getLocation(controlBlock);
            PortalPosition portalPosition = stargateAPI.getRegistry().getPortalPosition(location);
            if(portalPosition != null){
                continue;
            }
            double clickedBlockDistance = location.distance(clickedBlockLocation);
            if(closestBlockFromClickedBlockDistance == -1 || closestBlockFromClickedBlockDistance > clickedBlockDistance){
                closestBlockFromClickedBlockDistance = clickedBlockDistance;
                closestLocation = location;
                closestRelativeLocation = controlBlock;
            }
        }
        if(closestLocation == null){
            // TODO: Temp
            player.sendRawMessage("Could not find a control block");
            return;
        }

        // Tell Stargate the block belongs to Stargate, so that it can use the default behaviors
        PortalPosition portalPositionToRegister = new PortalPosition(PositionType.SIGN, closestRelativeLocation,"Stargate");
        StargateMechanics.log(Level.INFO, "Adding portal position of type" + portalPositionToRegister.getPositionType());
        stargateAPI.getRegistry().registerPortalPosition(portalPositionToRegister,closestLocation,portal);
        BlockState state = closestLocation.getBlock().getState();
        previousData.put(new BlockLocation(closestLocation),closestLocation.getBlock().getState());
        // TODO: currently hardcoded material for the sign, might wanna add something to this later on
        state.setType(Material.OAK_WALL_SIGN);
        WallSign signData = (WallSign) state.getBlockData();
        signData.setFacing(gate.getFacing());
        state.setBlockData(signData);
        state.update(true);
        portal.activate(player);
        portal.updateState();
    }

    public void onPluginDisable(){
        for(RealPortal portal : new ArrayList<>(removeSignMap.keySet())){
            removeSignsFromPortal(portal);
        }
    }
}
