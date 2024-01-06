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
import org.sgrewritten.stargatemechanics.locale.LanguageManager;
import org.sgrewritten.stargatemechanics.portal.MechanicsFlag;
import org.sgrewritten.stargatemechanics.redstone.OrRedstoneEngine;
import org.sgrewritten.stargatemechanics.redstone.RedstoneEngine;
import org.sgrewritten.stargatemechanics.signcoloring.ColoringOverrideLoader;
import org.sgrewritten.stargatemechanics.signcoloring.ColoringOverrideRegistry;
import org.sgrewritten.stargatemechanics.utils.ButtonUtils;
import org.sgrewritten.stargatemechanics.utils.SignUtils;
import org.sgrewritten.stargatemechanics.utils.redstone.RedstoneUtils;

import java.io.IOException;
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
    private ColoringOverrideRegistry colorRegistry;
    private LanguageManager languageManager;

    private static final String CONFIG = "config.yml";

    @Override
    public void onEnable() {
        instance = this;
        load();
    }

    private void load() {
        saveDefaultConfig();
        loadStargate();
        registerCustomFlags();
        PluginManager pluginManager = getServer().getPluginManager();
        this.engine = new OrRedstoneEngine(stargateAPI.getRegistry());
        RedstoneUtils.loadPortals(stargateAPI.getRegistry(),engine);
        this.colorRegistry = new ColoringOverrideRegistry();
        new ColoringOverrideLoader().load(stargateAPI.getRegistry(), colorRegistry);
        try {
            this.languageManager = new LanguageManager(this,this.getConfig().getString(ConfigurationOption.LOCALE.getKey()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        blockEventListener = new BlockEventListener(stargateAPI.getRegistry(),this, engine);
        pluginManager.registerEvents(new StargateEventListener(this, stargateAPI,engine, colorRegistry, languageManager), this);
        pluginManager.registerEvents(blockEventListener, this);
        pluginManager.registerEvents(new PluginEventListener(this), this);
        this.playerEventListener = new PlayerEventListener(stargateAPI,colorRegistry, this);
        pluginManager.registerEvents(playerEventListener,this);

        SignUtils.removeSignsFromNoSignPortals(stargateAPI.getRegistry());
        ButtonUtils.removeButtonsFromAllPortalsWithFlag(stargateAPI.getRegistry(), MechanicsFlag.REDSTONE_POWERED.getCharacterRepresentation());


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
            System.out.println("[" + level + "] " + message);
            return;
        }
        instance.getLogger().log(level,message);
    }
}