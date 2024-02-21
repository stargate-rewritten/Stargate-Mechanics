package org.sgrewritten.stargatemechanics.signcoloring;

import com.google.gson.JsonElement;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.PositionType;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.metadata.MetaData;

public class ColoringOverrideLoader {

    public void load(RegistryAPI registry, ColoringOverrideRegistry coloringOverrideRegistry) {
        for (PortalPosition portalPosition : registry.getPortalPositions().values()) {
            if (portalPosition.getPositionType() != PositionType.SIGN || !portalPosition.getPluginName().equals("Stargate")) {
                continue;
            }
            RealPortal portal = portalPosition.getPortal();
            JsonElement metaData = portalPosition.getMetadata(MetaData.SIGN_COLOR.name());
            if (metaData == null) {
                continue;
            }
            String dyeColorString = metaData.getAsString();
            if (dyeColorString.isBlank()) {
                continue;
            }
            DyeColor dyeColor = DyeColor.valueOf(dyeColorString);
            Location location = portal.getGate().getLocation(portalPosition.getRelativePositionLocation());
            coloringOverrideRegistry.registerOverride(location, new ColorOverride(dyeColor));
        }
    }
}
