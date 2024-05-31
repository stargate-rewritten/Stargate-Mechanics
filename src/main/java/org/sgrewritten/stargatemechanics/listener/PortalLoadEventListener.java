package org.sgrewritten.stargatemechanics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargate.api.event.portal.StargatePortalLoadEvent;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargate.api.network.portal.flag.PortalFlag;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.locale.MechanicsLanguageManager;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.portal.behavior.BehaviorInserter;
import org.sgrewritten.stargatemechanics.utils.ButtonUtils;
import org.sgrewritten.stargatemechanics.utils.SignUtils;

public class PortalLoadEventListener implements Listener {

    private final StargateAPI stargateAPI;
    private final StargateMechanics mechanics;
    private final MechanicsLanguageManager languageManager;

    public PortalLoadEventListener(StargateAPI stargateAPI, StargateMechanics mechanics, MechanicsLanguageManager languageManager){
        this.stargateAPI = stargateAPI;
        this.mechanics = mechanics;
        this.languageManager = languageManager;
    }

    @EventHandler(ignoreCancelled = true)
    void onStargatePortalLoad(StargatePortalLoadEvent event){
        if(event.getPortal() instanceof RealPortal realPortal){
            if(realPortal.hasFlag(PortalFlag.NO_SIGN)){
                SignUtils.removeSignsFromPortal(realPortal);
            }
            if(realPortal.hasFlag(MechanicsFlag.REDSTONE_POWERED)){
                ButtonUtils.removeButtonsFromPortal(realPortal);
            }
        }
    }
}
