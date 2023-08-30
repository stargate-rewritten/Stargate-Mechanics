package org.sgrewritten.stargatemechanics;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargate.api.network.Network;
import org.sgrewritten.stargate.api.network.portal.Portal;
import org.sgrewritten.stargate.api.network.portal.RealPortal;
import org.sgrewritten.stargatemechanics.listener.PlayerEventListener;
import org.sgrewritten.stargatemechanics.listener.StargateEventListener;
import org.sgrewritten.stargatemechanics.listener.BlockEventListener;
import org.sgrewritten.stargatemechanics.listener.PluginEventListener;
import org.sgrewritten.stargatemechanics.blockhandlerinterface.RedstoneFlagHandlerInterface;
import org.sgrewritten.stargatemechanics.utils.NoSignUtils;

import java.util.logging.Level;

/**
 * The main class for the Stargate-ExtraFlags add-on
 */
@SuppressWarnings("unused")
public class StargateMechanics extends JavaPlugin {

    BlockEventListener blockEventListener;
    private StargateAPI stargateAPI;
    static StargateMechanics instance;

    @Override
    public void onEnable() {
        instance = this;
        load();
    }

    private void load() {
        loadStargate();
        PluginManager pluginManager = getServer().getPluginManager();
        blockEventListener = new BlockEventListener(stargateAPI.getRegistry());
        pluginManager.registerEvents(new StargateEventListener(this, stargateAPI.getRegistry()), this);
        pluginManager.registerEvents(blockEventListener, this);
        pluginManager.registerEvents(new PluginEventListener(this), this);
        pluginManager.registerEvents(new PlayerEventListener(stargateAPI, this),this);
        this.stargateAPI.getMaterialHandlerResolver().addBlockHandlerInterface(new RedstoneFlagHandlerInterface(this));
        removeSignsFromNoSignPortals();
    }

    private void removeSignsFromNoSignPortals() {
        for(Network network : stargateAPI.getRegistry().getNetworkMap().values()){
            for(Portal portal : network.getAllPortals()){
                if(portal instanceof RealPortal realPortal){
                    NoSignUtils.removeSignsFromPortal(realPortal);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        
    }

    public void loadStargate() {
        ServicesManager servicesManager = this.getServer().getServicesManager();
        RegisteredServiceProvider<StargateAPI> stargateProvider = servicesManager.getRegistration(StargateAPI.class);
        if (stargateProvider != null) {
            this.stargateAPI = stargateProvider.getProvider();
        } else {
            throw new IllegalStateException("Unable to hook into Stargate. Make sure the Stargate plugin is installed " +
                    "and enabled.");
        }
    }

    public static void log(Level level, String message){
        if(instance == null){
            return;
        }
        instance.getLogger().log(level,message);
    }
}