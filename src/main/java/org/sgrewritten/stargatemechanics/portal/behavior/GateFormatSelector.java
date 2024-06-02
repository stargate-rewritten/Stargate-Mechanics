package org.sgrewritten.stargatemechanics.portal.behavior;

import org.sgrewritten.stargate.api.gate.GateFormatAPI;
import org.sgrewritten.stargate.api.gate.GateFormatRegistry;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.ConfigurationOption;
import org.sgrewritten.stargatemechanics.StargateMechanics;

import java.util.Locale;
import java.util.Optional;

public class GateFormatSelector {
    public static Optional<GateFormatAPI> selectFormat(RealPortal realPortal, StargateMechanics plugin) {
        String gateFormatSelectorBehavior = plugin.getConfig().getString(ConfigurationOption.GATE_FORMAT_SELECTION_BEHAVIOR.getKey());
        return switch (gateFormatSelectorBehavior.toLowerCase(Locale.ROOT)){
            case "origin" -> Optional.of(realPortal.getGate().getFormat());
            case "specific" -> Optional.ofNullable(GateFormatRegistry.getFormat(plugin.getConfig().getString(ConfigurationOption.SPECIFIED_GATE_FORMAT_NAME.getKey())));
            default -> {
                Optional<String> gateFormatName = GateFormatRegistry.getAllGateFormatNames().stream().findAny();
                yield gateFormatName.map(GateFormatRegistry::getFormat);
            }
        };
    }
}
