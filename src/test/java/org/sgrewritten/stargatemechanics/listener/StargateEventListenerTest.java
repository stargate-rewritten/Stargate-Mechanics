package org.sgrewritten.stargatemechanics.listener;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sgrewritten.stargate.Stargate;
import org.sgrewritten.stargate.api.event.portal.StargateCreatePortalEvent;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.exception.ParseException;
import org.sgrewritten.stargatemechanics.locale.LanguageManager;
import org.sgrewritten.stargatemechanics.metadata.MetaData;
import org.sgrewritten.stargatemechanics.metadata.MetaDataReader;
import org.sgrewritten.stargatemechanics.mock.GateMock;
import org.sgrewritten.stargatemechanics.mock.NetworkMock;
import org.sgrewritten.stargatemechanics.mock.PortalMock;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.redstone.OrRedstoneEngine;
import org.sgrewritten.stargatemechanics.signcoloring.ColoringOverrideRegistry;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;


class StargateEventListenerTest {

    private ServerMock server;
    private PlayerMock player;
    private GateMock gate;
    private NetworkMock network;
    private WorldMock world;
    private Location exit;
    private PortalMock portal;
    private StargateEventListener listener;
    private StargateMechanics stargateMechanics;
    private Stargate stargate;
    private OrRedstoneEngine engine;
    private ColoringOverrideRegistry coloringOverrideRegistry;
    private LanguageManager languageManager;

    @BeforeEach
    void setUp() throws IOException {
        this.server = MockBukkit.mock();
        this.world = server.addSimpleWorld("world");
        this.player = server.addPlayer("player");
        this.gate = new GateMock("nether.gate");
        this.network = new NetworkMock("network");
        this.exit = new Location(world,10,10,10);
        this.portal = new PortalMock(gate,network, exit);
        this.stargate = MockBukkit.load(Stargate.class);
        this.stargateMechanics = MockBukkit.load(StargateMechanics.class);
        this.engine = new OrRedstoneEngine(stargate.getRegistry());
        this.coloringOverrideRegistry = new ColoringOverrideRegistry();
        this.languageManager = new LanguageManager(stargateMechanics,"en");
        this.listener = new StargateEventListener(stargateMechanics,stargate.getRegistry(),engine,coloringOverrideRegistry,languageManager);
    }

    @AfterEach
    void tearDown(){
        MockBukkit.unmock();
    }
    @Test
    void onStargateCreateTest_CoordGate() throws ParseException, IOException {
        String coordString = "10<R<100";
        String[] lines = new String[] {
                "portal",
                "",
                "network",
                MechanicsFlag.COORD.getCharacterRepresentation() + "{"+coordString+"}"
        };
        portal.addFlag(MechanicsFlag.COORD.getCharacterRepresentation());
        StargateCreatePortalEvent event = new StargateCreatePortalEvent(player,portal,lines,false,null,0);
        listener.onStargateCreate(event);
        Assertions.assertEquals(coordString, MetaDataReader.getData(portal.getMetaData(), MetaData.DESTINATION_COORDS));
    }
}