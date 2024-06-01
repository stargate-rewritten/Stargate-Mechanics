package org.sgrewritten.stargatemechanics;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sgrewritten.stargate.Stargate;
import org.sgrewritten.stargate.api.gate.ExplicitGateBuilder;
import org.sgrewritten.stargate.api.gate.GateAPI;
import org.sgrewritten.stargate.api.gate.GateBuilder;
import org.sgrewritten.stargate.api.gate.GateFormatRegistry;
import org.sgrewritten.stargate.api.network.Network;
import org.sgrewritten.stargate.api.network.PortalBuilder;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.exception.GateConflictException;
import org.sgrewritten.stargate.exception.InvalidStructureException;
import org.sgrewritten.stargate.exception.NoFormatFoundException;
import org.sgrewritten.stargate.exception.TranslatableException;
import org.sgrewritten.stargate.network.StorageType;
import org.sgrewritten.stargate.property.PortalValidity;
import org.sgrewritten.stargate.property.StargateConstant;
import org.sgrewritten.stargate.thread.task.StargateAsyncTask;
import org.sgrewritten.stargate.thread.task.StargateTask;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.portal.behavior.GenerateBehavior;
import org.sgrewritten.stargatemechanics.utils.PortalUtil;

class StargateMechanicsTest {

    private Stargate stargate;
    private StargateMechanics mechanics;
    private ServerMock server;
    private WorldMock world;
    private Location topLeft;
    private PlayerMock owner;
    private GateAPI gate;

    @BeforeEach
    void setUp() throws InvalidStructureException, GateConflictException, NoFormatFoundException {
        this.server = MockBukkit.mock();
        this.stargate = MockBukkit.load(Stargate.class);
        this.mechanics = MockBukkit.load(StargateMechanics.class);
        this.world = server.addSimpleWorld("world");
        this.topLeft = new Location(world, 0, 10, 0);
        this.owner = server.addPlayer();
        GateBuilder gateBuilder = new ExplicitGateBuilder(stargate.getRegistry(), topLeft, GateFormatRegistry.getFormat(GateFormatRegistry.getAllGateFormatNames().stream().findAny().orElseThrow()));
        gateBuilder.setGenerateButtonPositions(true);
        this.gate = gateBuilder.build();
        gate.forceGenerateStructure();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void reloadPlugins_generateFlag() throws TranslatableException, InvalidStructureException, GateConflictException, NoFormatFoundException {
        PluginManager pluginManager = Bukkit.getPluginManager();
        String portalName = "hello world";
        generatePortal("G{10<r<12}", portalName);
        pluginManager.disablePlugin(mechanics);
        pluginManager.disablePlugin(stargate);

        pluginManager.enablePlugin(stargate);
        // Fix weird issue where the gate format did not generate properly
        stargate.getConfig().set(org.sgrewritten.stargate.api.config.ConfigurationOption.PORTAL_VALIDITY.getConfigNode(), PortalValidity.REPAIR);
        StargateTask.forceRunAllTasks();
        pluginManager.enablePlugin(mechanics);
        server.getScheduler().performTicks(30);
        Network network = stargate.getRegistry().getNetwork(StargateConstant.DEFAULT_NETWORK_ID, StorageType.LOCAL);
        Assertions.assertNotNull(network);
        RealPortal portal = (RealPortal) network.getPortal(portalName);
        PortalUtil.getAllPortals(stargate.getRegistry()).filter(realPortal -> realPortal.hasFlag(MechanicsFlag.GENERATE));
        Assertions.assertNotNull(portal);
        Assertions.assertInstanceOf(GenerateBehavior.class, portal.getBehavior());
    }

    RealPortal generatePortal(String flagString, String portalName) throws TranslatableException, InvalidStructureException, GateConflictException, NoFormatFoundException {
        PortalBuilder portalBuilder = new PortalBuilder(stargate, owner, portalName);
        portalBuilder.addEventHandling(owner);
        portalBuilder.setFlags(flagString);
        portalBuilder.setGate(gate);
        RealPortal portal = portalBuilder.build();
        StargateAsyncTask.forceRunAllTasks();
        return portal;
    }
}