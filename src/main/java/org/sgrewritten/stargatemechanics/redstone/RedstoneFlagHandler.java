package org.sgrewritten.stargatemechanics.redstone;

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

    }
}
