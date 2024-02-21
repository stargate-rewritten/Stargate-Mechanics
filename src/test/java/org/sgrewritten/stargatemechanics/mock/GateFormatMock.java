package org.sgrewritten.stargatemechanics.mock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.sgrewritten.stargate.api.gate.GateFormatAPI;
import org.sgrewritten.stargate.api.gate.structure.GateFormatStructureType;
import org.sgrewritten.stargate.api.gate.structure.GateStructure;
import org.sgrewritten.stargate.api.vectorlogic.VectorOperation;

import java.util.List;

public class GateFormatMock implements GateFormatAPI {

    private final String fileName;
    private int height = 0;
    private int width = 0;

    public GateFormatMock(String fileName){
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public List<BlockVector> getControlBlocks() {
        return List.of(new BlockVector(1,-1,0), new BlockVector(1,-1,2));
    }

    @Override
    public Material getIrisMaterial(boolean b) {
        return null;
    }

    @Override
    public boolean isIronDoorBlockable() {
        return false;
    }

    @Override
    public BlockVector getExit() {
        return null;
    }

    @Override
    public GateStructure getStructure(GateFormatStructureType gateFormatStructureType) {
        return null;
    }

    @Override
    public boolean matches(VectorOperation vectorOperation, Location location) {
        return false;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    public void setWidth(int width){
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
