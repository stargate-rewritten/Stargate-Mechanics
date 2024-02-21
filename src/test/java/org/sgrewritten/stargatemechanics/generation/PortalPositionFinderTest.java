package org.sgrewritten.stargatemechanics.generation;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sgrewritten.stargate.exception.InvalidStructureException;
import org.sgrewritten.stargatemechanics.exception.GateGenerationFault;
import org.sgrewritten.stargatemechanics.mock.GateFormatMock;
import org.sgrewritten.stargatemechanics.mock.GateMock;
import org.sgrewritten.stargatemechanics.mock.RegistryMock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PortalPositionFinderTest {

    private ServerMock server;
    private WorldMock world;
    private PortalPositionFinder portalPositionsFinder;
    private GateFormatMock gateFormat;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("world");
        gateFormat = new GateFormatMock("something.gate");
        portalPositionsFinder = new PortalPositionFinder(new Location(world, 0,0,0), gateFormat, new RegistryMock());
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void sortTestPoints() {
        Location center = new Location(world, 0,0,0);
        Location locationFurthestAway = new Location(world,2,0,0);
        Location closestLocation = new Location(world,1,0,0);
        List<Location> locationsToSort = new ArrayList<>(List.of(locationFurthestAway,closestLocation));
        portalPositionsFinder.sortTestPoints(locationsToSort, center);
        Assertions.assertEquals(closestLocation, locationsToSort.get(0));
        Assertions.assertEquals(locationFurthestAway, locationsToSort.get(1));
    }

    @Test
    void gateIsInAirBlocks_outsideWorld() {
        Assertions.assertFalse(portalPositionsFinder.gateIsInAirBlocks(new GateMock("something.gate",new Location(world,0,-1,0))));
    }

    @Test
    void gateIsInAirBlocks_notInAir() {
        Assertions.assertFalse(portalPositionsFinder.gateIsInAirBlocks(new GateMock("something.gate",new Location(world,0,5,0))));
    }

    @Test
    void gateIsInAirBlocks_inAir() {
        Assertions.assertTrue(portalPositionsFinder.gateIsInAirBlocks(new GateMock("something.gate",new Location(world,0,20,0))));
    }

    @Test
    void findModifiedStartingPoint() {
        gateFormat.setHeight(4);
        Location modifiedStartingPoint = portalPositionsFinder.findModifiedStartingPoint(new Location(world, 1,5,0));
        Assertions.assertEquals(8, modifiedStartingPoint.getBlockY());
        Assertions.assertEquals(1, modifiedStartingPoint.getBlockX());
        Assertions.assertEquals(0, modifiedStartingPoint.getBlockZ());
    }

    @Test
    void findModifiedStartingPoint_closeOutsideWorld() {
        gateFormat.setHeight(4);
        WorldMock worldMock = new WorldMock(Material.COAL_BLOCK,0);
        server.addWorld(worldMock);
        Location modifiedStartingPoint = portalPositionsFinder.findModifiedStartingPoint(new Location(worldMock, 1,0,0));
        Assertions.assertEquals(4, modifiedStartingPoint.getBlockY());
        Assertions.assertEquals(1, modifiedStartingPoint.getBlockX());
        Assertions.assertEquals(0, modifiedStartingPoint.getBlockZ());
    }
}