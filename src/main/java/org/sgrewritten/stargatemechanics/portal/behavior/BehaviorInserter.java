package org.sgrewritten.stargatemechanics.portal.behavior;

import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.locale.MechanicsLanguageManager;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.utils.CoordinateParser;

public class BehaviorInserter {

    private BehaviorInserter() {
        throw new IllegalStateException("Utility class");
    }

    public static void insertMechanicsBehavior(RealPortal realPortal, StargateAPI stargateAPI, StargateMechanics plugin, MechanicsLanguageManager mechanicsLanguageManager) {
        String previousDestination = realPortal.getBehavior().getDestinationName();
        if (realPortal.hasFlag(MechanicsFlag.GENERATE)) {
            boolean isRandomDestination = CoordinateParser.isRandomCoordinateDestination(realPortal);
            realPortal.setBehavior(new GenerateBehavior(stargateAPI, plugin, mechanicsLanguageManager, previousDestination, isRandomDestination));
        }
    }
}
