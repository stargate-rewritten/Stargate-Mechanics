package org.sgrewritten.stargatemechanics.mock;

import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.network.portal.behavior.PortalBehavior;
import org.sgrewritten.stargate.api.network.portal.flag.PortalFlag;
import org.sgrewritten.stargate.api.network.portal.formatting.data.LineData;

public class MockBehavior implements PortalBehavior {
    @Override
    public void onButtonClick(@NotNull PlayerInteractEvent playerInteractEvent) {

    }

    @Override
    public void onSignClick(@NotNull PlayerInteractEvent playerInteractEvent) {

    }

    @Override
    public void onDestroy() {

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
        return null;
    }

    @Override
    public void assignPortal(@NotNull RealPortal realPortal) {

    }

    @Override
    public @Nullable String getDestinationName() {
        return "";
    }
}
