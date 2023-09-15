package org.sgrewritten.stargatemechanics.mock;

import org.bukkit.Material;
import org.bukkit.util.BlockVector;
import org.sgrewritten.stargate.api.gate.GateFormatAPI;

import java.util.List;

public class GateFormatMock implements GateFormatAPI {

    private final String fileName;

    public GateFormatMock(String fileName){
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public List<BlockVector> getControlBlocks() {
        return null;
    }

    @Override
    public Material getIrisMaterial(boolean b) {
        return null;
    }
}
