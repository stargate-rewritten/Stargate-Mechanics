package org.stargate.stargatemechanics;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.sgrewritten.stargate.api.StargateAPI;
import org.stargate.stargatemechanics.listener.StargateListener;

/**
 * The main class for the Stargate-ExtraFlags add-on
 */
@SuppressWarnings("unused")
public class StargateMechanics extends JavaPlugin {

    @Override
    public void onEnable() {
        //Get the Stargate API
        ServicesManager servicesManager = this.getServer().getServicesManager();
        RegisteredServiceProvider<StargateAPI> stargateProvider = servicesManager.getRegistration(StargateAPI.class);
        if (stargateProvider != null) {
            StargateAPI stargateAPI = stargateProvider.getProvider();

            PluginManager pluginManager = getServer().getPluginManager();
            pluginManager.registerEvents(new StargateListener(this), this);
        } else {
            throw new IllegalStateException("Unable to hook into Stargate. Make sure the Stargate plugin is installed " +
                    "and enabled.");
        }
    }

    @Override
    public void onDisable() {
    }
}