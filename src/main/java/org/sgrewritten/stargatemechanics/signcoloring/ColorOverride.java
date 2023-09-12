package org.sgrewritten.stargatemechanics.signcoloring;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.sgrewritten.stargatemechanics.utils.ColorConverter;

public record ColorOverride(ChatColor textColor, ChatColor pointerColor) {

    public ColorOverride(DyeColor dyeColor) {
        this(ColorConverter.getChatColorFromDyeColor(dyeColor),ColorConverter.getChatColorFromDyeColor(dyeColor));
    }
}
