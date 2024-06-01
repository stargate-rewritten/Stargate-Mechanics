package org.sgrewritten.stargatemechanics.generation;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;
import org.sgrewritten.stargate.api.gate.ExplicitGateBuilder;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.gate.GateFormatAPI;
import org.sgrewritten.stargate.api.gate.GateStructureType;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.BlockLocation;
import org.sgrewritten.stargate.exception.InvalidStructureException;
import org.sgrewritten.stargatemechanics.exception.GateGenerationFault;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PortalPositionFinder {

    private final Location startingPoint;
    private final GateFormatAPI gateFormat;
    private final RegistryAPI registryAPI;
    private final List<BlockFace> facingOrder = new ArrayList<>();
    private final static List<BlockFace> POSSIBLE_FACES = List.of(BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST);

    public PortalPositionFinder(Location startingPoint, GateFormatAPI gateFormat, RegistryAPI registryAPI) {
        this.startingPoint = startingPoint;
        this.gateFormat = gateFormat;
        this.registryAPI = registryAPI;
        for (int i = 0; i < POSSIBLE_FACES.size(); i++) {
            List<BlockFace> facesToChoseFrom = new ArrayList<>(POSSIBLE_FACES);
            facesToChoseFrom.removeAll(facingOrder);
            facingOrder.add(facesToChoseFrom.get(new Random().nextInt(facesToChoseFrom.size())));
        }
    }

    public GateAPI getValidGate(int searchBoxHalfWidth) throws GateGenerationFault, InvalidStructureException {
        Location modifiedStartingPoint = findModifiedStartingPoint(startingPoint);
        List<Location> testPoints = getTestPoints(modifiedStartingPoint, searchBoxHalfWidth);
        sortTestPoints(testPoints, modifiedStartingPoint);
        for (Location testPoint : testPoints) {
            ExplicitGateBuilder gateBuilder = new ExplicitGateBuilder(registryAPI, testPoint, gateFormat);
            for (BlockFace blockFace : facingOrder) {
                GateAPI testGate = gateBuilder.setFacing(blockFace).build();
                if (gateIsInAirBlocks(testGate)) {
                    return testGate;
                }
            }
        }
        throw new GateGenerationFault("Could not find any viable gate position for location: " + modifiedStartingPoint);
    }

    private List<Location> getTestPoints(Location startingPoint, int searchBoxHalfWidth) {
        startingPoint = new Location(startingPoint.getWorld(), startingPoint.getBlockX(), startingPoint.getBlockY(), startingPoint.getBlockZ());
        List<Location> testPoints = new ArrayList<>();
        for (int x = -searchBoxHalfWidth; x <= searchBoxHalfWidth; x++) {
            for (int y = -searchBoxHalfWidth; y <= searchBoxHalfWidth; y++) {
                for (int z = -searchBoxHalfWidth; z <= searchBoxHalfWidth; z++) {
                    testPoints.add(startingPoint.clone().add(new BlockVector(x, y, z)));
                }
            }
        }
        return testPoints;
    }

    void sortTestPoints(List<Location> testPoints, Location modifiedStartingPoint) {
        testPoints.sort((location1, location2) -> {
            Location diff1 = location1.clone().subtract(modifiedStartingPoint);
            Location diff2 = location2.clone().subtract(modifiedStartingPoint);
            return (Math.abs(diff1.getBlockX()) + Math.abs(diff1.getBlockY()) + Math.abs(diff1.getBlockZ()))
                    - (Math.abs(diff2.getBlockX()) + Math.abs(diff2.getBlockY()) + Math.abs(diff2.getBlockZ()));
        });
    }

    boolean gateIsInAirBlocks(GateAPI gateAPI) {
        for (GateStructureType gateStructureType : GateStructureType.values()) {
            for (BlockLocation location : gateAPI.getLocations(gateStructureType)) {
                if (blockIsNotAirOrNotOutsideWorld(location.getLocation())) {
                    return false;
                }
            }
        }
        for (BlockVector vector : gateFormat.getControlBlocks()) {
            if (blockIsNotAirOrNotOutsideWorld(gateAPI.getLocation(vector))) {
                return false;
            }
        }
        return true;
    }

    Location findModifiedStartingPoint(Location startingPoint) {
        int height = gateFormat.getHeight();
        for (int dY = 0; dY <= height; dY++) {
            Location modifiedStartingPoint = startingPoint.clone().add(new BlockVector(0, dY, 0));
            if (isAirBlocksUnderLocationForDistance(height, modifiedStartingPoint)) {
                return modifiedStartingPoint;
            }
        }
        return startingPoint.clone().add(new BlockVector(0, height - 1, 0));
    }

    private boolean isAirBlocksUnderLocationForDistance(int distance, Location location) {
        for (int dY = -1; dY > -distance; dY--) {
            Location locationToCheckFor = location.clone().add(new BlockVector(0, dY, 0));
            if (blockIsNotAirOrNotOutsideWorld(locationToCheckFor)) {
                return false;
            }
        }
        return true;
    }

    private boolean blockIsNotAirOrNotOutsideWorld(Location location) {
        return location.getWorld().getMinHeight() > location.getBlockY() || !location.getBlock().getType().isAir();
    }
}
