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
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.exception.ParseException;
import org.sgrewritten.stargatemechanics.locale.MechanicsLanguageManager;
import org.sgrewritten.stargatemechanics.metadata.MetaData;
import org.sgrewritten.stargatemechanics.mock.GateMock;
import org.sgrewritten.stargatemechanics.mock.MockBehavior;
import org.sgrewritten.stargatemechanics.mock.NetworkMock;
import org.sgrewritten.stargatemechanics.mock.PortalMock;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.redstone.OrRedstoneEngine;
import org.sgrewritten.stargatemechanics.signcoloring.ColoringOverrideRegistry;

import java.io.IOException;
import java.util.HashSet;


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
    private MechanicsLanguageManager mechanicsLanguageManager;

    @BeforeEach
    void setUp() throws IOException {
        this.server = MockBukkit.mock();
        this.world = server.addSimpleWorld("world");
        this.player = server.addPlayer("player");
        this.gate = new GateMock("nether.gate", new Location(world, 10, 14, 10));
        this.network = new NetworkMock("network");
        this.exit = new Location(world, 10, 10, 10);
        this.portal = new PortalMock(gate, network, exit);
        this.stargate = MockBukkit.load(Stargate.class);
        this.stargateMechanics = MockBukkit.load(StargateMechanics.class);
        this.engine = new OrRedstoneEngine(stargate.getRegistry());
        this.coloringOverrideRegistry = new ColoringOverrideRegistry();
        this.mechanicsLanguageManager = new MechanicsLanguageManager(stargateMechanics, "en");
        this.listener = new StargateEventListener(stargateMechanics, stargate, engine, coloringOverrideRegistry, mechanicsLanguageManager, new HashSet<>());
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void onStargateCreateTest_CoordGate() throws ParseException, IOException {
        String coordString = "10<R<100";
        String timerString = "1D";
        String[] lines = new String[]{
                "portal",
                "",
                "network",
                MechanicsFlag.GENERATE.getCharacterRepresentation() + "{" + coordString + "}" + MechanicsFlag.OPEN_TIMER.getCharacterRepresentation() + "{" + timerString + "}"
        };
        portal.addFlag(MechanicsFlag.GENERATE);
        portal.addFlag(MechanicsFlag.OPEN_TIMER);
        StargateCreatePortalEvent event = new StargateCreatePortalEvent(player, portal, lines, false, null, 0);
        listener.onStargateCreate(event);
        Assertions.assertEquals(coordString, portal.getMetadata(MetaData.DESTINATION_COORDS.name()).getAsString());
        Assertions.assertEquals(timerString, portal.getMetadata(MetaData.OPEN_COUNTDOWN.name()).getAsString());
    }

    @Test
    void onStargateCreateTest_InvalidRandomCoordArgument() {
        String coordString = "10<R<g";
        String[] lines = new String[]{
                "portal",
                "",
                "network",
                MechanicsFlag.GENERATE.getCharacterRepresentation() + "{" + coordString + "}"
        };
        portal.addFlag(MechanicsFlag.GENERATE);
        StargateCreatePortalEvent event = new StargateCreatePortalEvent(player, portal, lines, false, null, 0);
        listener.onStargateCreate(event);
        Assertions.assertInstanceOf(MockBehavior.class, ((RealPortal) event.getPortal()).getBehavior());
    }

    @Test
    void onStargateCreateTest_InvalidCountdownArgument() {
        String countdownString = "1q";
        String[] lines = new String[]{
                "portal",
                "",
                "network",
                MechanicsFlag.OPEN_TIMER.getCharacterRepresentation() + "{" + countdownString + "}"
        };
        portal.addFlag(MechanicsFlag.OPEN_TIMER);
        StargateCreatePortalEvent event = new StargateCreatePortalEvent(player, portal, lines, false, null, 0);
        listener.onStargateCreate(event);
        Assertions.assertFalse(portal.hasFlag(MechanicsFlag.OPEN_TIMER));
    }
}