package org.sgrewritten.stargatemechanics.signcoloring;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.sgrewritten.stargate.api.network.RegistryAPI;
import org.sgrewritten.stargate.api.network.portal.PortalPosition;
import org.sgrewritten.stargate.api.network.portal.PositionType;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.exception.ParseException;
import org.sgrewritten.stargatemechanics.metadata.MetaData;
import org.sgrewritten.stargatemechanics.metadata.MetaDataReader;

public class ColoringOverrideLoader {

    public void load(RegistryAPI registry, ColoringOverrideRegistry coloringOverrideRegistry){
        for(PortalPosition portalPosition : registry.getPortalPositions().values()){
            if(portalPosition.getPositionType() != PositionType.SIGN || !portalPosition.getPluginName().equals("Stargate")){
                continue;
            }
            RealPortal portal = registry.getPortalFromPortalPosition(portalPosition);
            String metaData = portalPosition.getMetaData(portal);
            if(metaData == null || metaData.isBlank()){
                continue;
            }
            try {
                DyeColor dyeColor = DyeColor.valueOf(MetaDataReader.getData(metaData, MetaData.SIGN_COLOR));
                Location location = portal.getGate().getLocation(portalPosition.getRelativePositionLocation());
                coloringOverrideRegistry.registerOverride(location, new ColorOverride(dyeColor));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
