package org.sgrewritten.stargatemechanics.portal.behavior;

import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.ConfigurationOption;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.locale.MechanicsLanguageManager;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.utils.CoordinateParser;

public class BehaviorInserter {

    private BehaviorInserter() {
        throw new IllegalStateException("Utility class");
    }

    public static void insertMechanicsBehavior(RealPortal realPortal, StargateAPI stargateAPI, StargateMechanics plugin, MechanicsLanguageManager mechanicsLanguageManager) {
        if (realPortal.hasFlag(MechanicsFlag.GENERATE)) {
            boolean isRandomDestination = CoordinateParser.isRandomCoordinateDestination(realPortal);
            String previousDestination = realPortal.getBehavior().getDestinationName();
            String portalGenerationFlagString;
            if (isRandomDestination) {
                portalGenerationFlagString = plugin.getConfig().getString(ConfigurationOption.RANDOM_GENERATED_GATE_FLAG_STRING.getKey());
            } else {
                portalGenerationFlagString = plugin.getConfig().getString(ConfigurationOption.GENERATED_GATE_FLAG_STRING.getKey());
            }
            realPortal.setBehavior(new GenerateBehavior(stargateAPI, plugin, mechanicsLanguageManager, previousDestination, isRandomDestination, portalGenerationFlagString));
            realPortal.redrawSigns();
        }
    }
}
