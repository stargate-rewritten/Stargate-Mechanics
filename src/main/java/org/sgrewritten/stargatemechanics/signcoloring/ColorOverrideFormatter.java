package org.sgrewritten.stargatemechanics.signcoloring;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargate.api.network.portal.format.SignLine;
import org.sgrewritten.stargate.api.network.portal.format.StargateComponent;

import java.util.List;

public class ColorOverrideFormatter {

    private static void changeColorOfComponent(StargateComponent component, ChatColor color){
        String text = ChatColor.stripColor(component.getLegacyText());
        component.setLegacyText(color + text);
    }
    public static void formatFromOverride(@Nullable ColorOverride colorOverride, SignLine line){
        if(colorOverride == null){
            return;
        }
        List<StargateComponent> components = line.getComponents();
        if(components.size() == 3){
            changeColorOfComponent(components.get(0),colorOverride.pointerColor());
            changeColorOfComponent(components.get(1),colorOverride.textColor());
            changeColorOfComponent(components.get(2),colorOverride.pointerColor());
        } else {
            for(StargateComponent component : components){
                changeColorOfComponent(component,colorOverride.textColor());
            }
        }
    }
}
