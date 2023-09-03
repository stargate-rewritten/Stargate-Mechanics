package org.sgrewritten.stargatemechanics.flags;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.sgrewritten.stargate.api.gate.GateStructureType;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.PositionType;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.metadata.MetaDataProperty;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;

import java.util.*;
import java.util.logging.Level;

public class RedstoneFlagHandler implements Runnable{

    private final BlockRedstoneEvent event;
    private final RegistryAPI registry;
    private final StargateMechanics plugin;

    public RedstoneFlagHandler(BlockRedstoneEvent event, RegistryAPI registry, StargateMechanics plugin){
        this.event = event;
        this.registry = registry;
        this.plugin = plugin;
    }
    @Override
    public void run(){
        // Filter away any changes in current that only modifies the signal strength
        if((event.getNewCurrent() == 0) == (event.getOldCurrent() == 0)){
            return;
        }
        Collection<BlockLocation> effectedBlocks = new ArrayList<>();
        if(event.getBlock().getType() == Material.REDSTONE_WIRE){
            effectedBlocks = getEffectedBlocksFromRedstoneWire(event.getBlock());
        }
        Set<RealPortal> effectedPortals = new HashSet<>();
        for(BlockLocation effectedBlock : effectedBlocks){
            if(!effectedBlock.getLocation().getBlock().getType().isSolid()){
                continue;
            }
            effectedPortals.addAll(registry.getPortalsFromTouchingBlock(effectedBlock.getLocation(), GateStructureType.FRAME));
            RealPortal possiblePortal = registry.getPortal(effectedBlock, GateStructureType.FRAME);
            if(possiblePortal != null){
                effectedPortals.add(possiblePortal);
            }
        }
        for(RealPortal portal : effectedPortals){
            if(portal.hasFlag(MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation())){
                if(event.getNewCurrent() == 0){
                    portal.close(true);
                } else {
                    portal.open(null);
                }
            }
        }
    }

    private Set<BlockLocation> getEffectedBlocksFromRedstoneWire(Block redstoneWireBlock){
        BlockData data = redstoneWireBlock.getBlockData();
        Set<BlockLocation> output = new HashSet<>();
        if(data instanceof RedstoneWire redstoneWire){
            Location location = redstoneWireBlock.getLocation();
            for(BlockFace face : redstoneWire.getAllowedFaces()){
                if(redstoneWire.getFace(face) == RedstoneWire.Connection.NONE){
                    continue;
                }
                Location otherLocation = location.clone().add(face.getDirection());
                output.add(new BlockLocation(otherLocation));
            }
        }
        return output;
    }
}
