package org.sgrewritten.stargatemechanics;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargatemechanics.listener.PlayerEventListener;
import org.sgrewritten.stargatemechanics.listener.StargateEventListener;
import org.sgrewritten.stargatemechanics.listener.BlockEventListener;
import org.sgrewritten.stargatemechanics.listener.PluginEventListener;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.redstone.OrRedstoneEngine;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;
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
    private PlayerEventListener playerEventListener;
    private RedstoneEngine engine;

    @Override
    public void onEnable() {
        instance = this;
        load();
    }

    private void load() {
        loadStargate();
        registerCustomFlags();
        PluginManager pluginManager = getServer().getPluginManager();
        this.engine = new OrRedstoneEngine(stargateAPI.getRegistry());
        blockEventListener = new BlockEventListener(stargateAPI.getRegistry(),this, engine);
        pluginManager.registerEvents(new StargateEventListener(this, stargateAPI.getRegistry()), this);
        pluginManager.registerEvents(blockEventListener, this);
        pluginManager.registerEvents(new PluginEventListener(this), this);
        this.playerEventListener = new PlayerEventListener(stargateAPI, this);
        pluginManager.registerEvents(playerEventListener,this);
        NoSignUtils.removeSignsFromNoSignPortals(stargateAPI.getRegistry());
    }

    private void registerCustomFlags() {
        for(MechanicsFlag flag : MechanicsFlag.values()){
            stargateAPI.getMaterialHandlerResolver().registerCustomFlag(flag.getCharacterRepresentation());
        }
    }

    @Override
    public void onDisable() {
        playerEventListener.onPluginDisable();
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