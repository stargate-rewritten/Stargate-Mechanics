package org.sgrewritten.stargatemechanics.portal.behavior;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargate.api.formatting.LanguageManager;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.flag.PortalFlag;
import org.sgrewritten.stargate.api.network.portal.formatting.data.LineData;
import org.sgrewritten.stargate.api.network.portal.behavior.AbstractPortalBehavior;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;

public class RandomCoordBehavior extends AbstractPortalBehavior {
    private final @Nullable String destinationString;

    public RandomCoordBehavior(@NotNull LanguageManager languageManager, @Nullable String destinationString) {
        super(languageManager);
        this.destinationString = destinationString;
    }

    @Override
    public void update() {

    }

    @Override
    public @Nullable Portal getDestination() {
        return null;
    }

    @Override
    public @NotNull LineData @NotNull [] getLines() {
        return new LineData[0];
    }

    @Override
    public @NotNull PortalFlag getAttachedFlag() {
        return MechanicsFlag.RANDOM_COORD;
    }
}
