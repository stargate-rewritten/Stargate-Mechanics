package org.sgrewritten.stargatemechanics.portal.behavior;

import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargate.api.network.Network;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.network.StorageType;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.locale.MechanicsLanguageManager;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;

import java.util.stream.Stream;

public class BehaviorInserter {

    private BehaviorInserter() {
        throw new IllegalStateException("Utility class");
    }

    public static void insertMechanicsBehavior(RealPortal realPortal, StargateAPI stargateAPI, StargateMechanics plugin, MechanicsLanguageManager mechanicsLanguageManager) {
        String previousDestination = realPortal.getBehavior().getDestinationName();

        if (realPortal.hasFlag(MechanicsFlag.COORD)) {
            realPortal.setBehavior(new CoordBehavior(stargateAPI, plugin, mechanicsLanguageManager, previousDestination));
        }
        if (realPortal.hasFlag(MechanicsFlag.RANDOM_COORD)) {
            realPortal.setBehavior(new RandomCoordBehavior(stargateAPI.getLanguageManager(), previousDestination));
        }
    }
}
