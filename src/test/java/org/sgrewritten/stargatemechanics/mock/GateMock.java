package org.sgrewritten.stargatemechanics.mock;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.gate.GateFormatAPI;
import org.sgrewritten.stargate.api.gate.GateStructureType;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.PositionType;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.network.portal.formatting.SignLine;
import org.sgrewritten.stargate.api.network.portal.formatting.data.LineData;
import org.sgrewritten.stargate.exception.GateConflictException;
import org.sgrewritten.stargatemechanics.mock.GateFormatMock;

import java.util.ArrayList;
import java.util.List;

public class GateMock implements GateAPI {

    private final GateFormatMock gateFormat;
    private final BlockFace facing;
    private final Location topLeft;

    public GateMock(String formatName, BlockFace facing, Location topLeft){
        this.gateFormat = new GateFormatMock(formatName);
        this.facing = facing;
        this.topLeft = topLeft;
    }

    public GateMock(String formatName, Location topLeft){
        this(formatName,BlockFace.EAST, topLeft);
    }

    @Override
    public void drawControlMechanisms(LineData[] lineData) {

    }

    @Override
    public void redrawPosition(PortalPosition portalPosition, @Nullable LineData[] lineData) {

    }

    @Override
    public List<PortalPosition> getPortalPositions() {
        return new ArrayList<>();
    }

    @Override
    public List<BlockLocation> getLocations(GateStructureType gateStructureType) {
        List<BlockVector> vectorList = switch (gateStructureType){
            case IRIS -> List.of(new BlockVector(0,-1,1), new BlockVector(0,-2,1));
            case FRAME -> List.of(new BlockVector(0,0,1),new BlockVector(0,-1,0), new BlockVector(0,-1,2),
                    new BlockVector(0,-2,0), new BlockVector(0,-2,2), new BlockVector(0,-3,1));
        };
        List<BlockLocation> output = new ArrayList<>();
        for(BlockVector vector : vectorList){
            output.add(new BlockLocation(topLeft.clone().add(vector)));
        }
        return output;
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public Location getExit() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public GateFormatAPI getFormat() {
        return this.gateFormat;
    }

    @Override
    public BlockFace getFacing() {
        return this.facing;
    }

    @Override
    public Vector getRelativeVector(Location location) {
        return null;
    }

    @Override
    public boolean getFlipZ() {
        return false;
    }

    @Override
    public Location getLocation(@NotNull Vector vector) {
        return topLeft.clone().add(vector);
    }

    @Override
    public boolean isValid() throws GateConflictException {
        return false;
    }

    @Override
    public void calculatePortalPositions(boolean b) {

    }

    @Override
    public Location getTopLeft() {
        return topLeft;
    }

    @Override
    public PortalPosition addPortalPosition(Location location, PositionType positionType, String s) {
        return null;
    }

    @Override
    public void addPortalPosition(PortalPosition portalPosition) {

    }

    @Override
    public @Nullable PortalPosition removePortalPosition(Location location) {
        return null;
    }

    @Override
    public void removePortalPosition(PortalPosition portalPosition) {

    }

    @Override
    public void forceGenerateStructure() {

    }

    @Override
    public void assignPortal(RealPortal realPortal) {

    }

    @Override
    public RealPortal getPortal() {
        return null;
    }
}
