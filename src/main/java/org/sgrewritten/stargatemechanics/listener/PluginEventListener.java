package org.sgrewritten.stargatemechanics.listener;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;
import org.sgrewritten.stargate.api.StargateAPI;
import org.sgrewritten.stargatemechanics.StargateMechanics;

public class PluginEventListener implements Listener {
    static boolean hasRegisteredFlags = false;
    private @NotNull StargateMechanics stargateMechanics;

    public PluginEventListener(@NotNull StargateMechanics stargateMechanics) {
        this.stargateMechanics = Objects.requireNonNull(stargateMechanics);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("Stargate") && event.getPlugin() instanceof StargateAPI) {
            Bukkit.getPluginManager().disablePlugin(stargateMechanics);
        }
    }
}
