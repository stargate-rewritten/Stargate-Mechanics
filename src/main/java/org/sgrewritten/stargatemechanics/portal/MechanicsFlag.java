package org.sgrewritten.stargatemechanics.portal;

import org.sgrewritten.stargate.api.network.portal.flag.CustomFlag;
import org.sgrewritten.stargate.api.network.portal.flag.PortalFlag;

import java.util.HashSet;
import java.util.Set;

public class MechanicsFlag {

    private static final Set<PortalFlag> registeredFlags = new HashSet<>();
    public static final PortalFlag REDSTONE_POWERED = registerFlag('E');
    public static final PortalFlag COORD = registerFlag('G').modify(false, true);
    public static final PortalFlag RANDOM_COORD = registerFlag('J').modify(false, true);
    public static final PortalFlag OPEN_TIMER = registerFlag('C');
    public static final PortalFlag DESTROY_TIMER = registerFlag('D');

    private MechanicsFlag() {
        throw new IllegalStateException("Utility class");
    }

    private static CustomFlag registerFlag(char character) {
        CustomFlag portalFlag = CustomFlag.getOrCreate(character);
        registeredFlags.add(portalFlag);
        return portalFlag;
    }

    public static Set<PortalFlag> values() {
        return new HashSet<>(registeredFlags);
    }
}
