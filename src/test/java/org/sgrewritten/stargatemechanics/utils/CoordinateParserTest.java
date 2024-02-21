package org.sgrewritten.stargatemechanics.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import jakarta.json.JsonObject;
import net.joshka.junit.json.params.JsonFileSource;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.sgrewritten.stargatemechanics.exception.ParseException;
import org.sgrewritten.stargatemechanics.mock.GateMock;
import org.sgrewritten.stargatemechanics.mock.NetworkMock;
import org.sgrewritten.stargatemechanics.mock.PortalMock;

class CoordinateParserTest {

    private ServerMock server;
    private WorldMock world;
    private GateMock gate;
    private NetworkMock network;
    private Location exit;
    private PortalMock portal;

    @BeforeEach
    void setUp(){
        this.server = MockBukkit.mock();
        this.world = new WorldMock(Material.GRASS_BLOCK, 3);
        server.addWorld(world);
        this.gate = new GateMock("nether.gate", BlockFace.NORTH, new Location(world, 10, 5, 10));
        this.network = new NetworkMock("network");
        this.exit = new Location(world,10,10,10);
        this.portal = new PortalMock(gate,network,new Location(world,0,1,0));
    }

    @AfterEach
    void tearDown(){
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @JsonFileSource(resources = "/coordinateExpressions.json")
    void getLocationFromExpression(JsonObject object) throws ParseException {
        Location destination = CoordinateParser.getLocationFromExpression(object.getString("expression").toUpperCase(), portal);
        Assertions.assertEquals(object.getInt("x"),destination.getBlockX());
        Assertions.assertEquals(object.getInt("y"),destination.getBlockY());
        Assertions.assertEquals(object.getInt("z"),destination.getBlockZ());
    }

    @ParameterizedTest
    @JsonFileSource(resources = "/invalidCoordinateExpressions.json")
    void getLocationExpression_Invalid(JsonObject object) {
        Assertions.assertThrows(ParseException.class, () -> CoordinateParser.getLocationFromExpression(object.getString("expression").toUpperCase(), portal));
    }
    @ParameterizedTest
    @JsonFileSource(resources = "/randomCoordinateExpressions.json")
    void getRandomLocationFromExpression(JsonObject object) {
        Assertions.assertDoesNotThrow(() -> CoordinateParser.getRandomLocationFromExpression(object.getString("expression"), portal));
    }

    @ParameterizedTest
    @JsonFileSource(resources = "/invalidRandomCoordinateExpressions.json")
    void getRandomLocationFromExpression_Invalid(JsonObject object) {
        Assertions.assertThrows(ParseException.class, () -> CoordinateParser.getRandomLocationFromExpression(object.getString("expression"), portal));
    }
}