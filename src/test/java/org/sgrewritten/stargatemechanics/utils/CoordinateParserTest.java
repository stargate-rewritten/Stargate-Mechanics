package org.sgrewritten.stargatemechanics.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import jakarta.json.JsonObject;
import net.joshka.junit.json.params.JsonFileSource;
import org.bukkit.Location;
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
        this.world = server.addSimpleWorld("name");
        this.gate = new GateMock("nether.gate", BlockFace.NORTH, new Location(world, 0, 5, 0));
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
        Assertions.assertEquals(destination.getBlockX(),object.getInt("x"));
        Assertions.assertEquals(destination.getBlockY(),object.getInt("y"));
        Assertions.assertEquals(destination.getBlockZ(),object.getInt("z"));
    }

    @Test
    void getRandomLocationFromExpression() {

    }
}