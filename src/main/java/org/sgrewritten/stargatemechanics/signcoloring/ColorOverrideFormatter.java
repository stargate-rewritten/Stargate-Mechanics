package org.sgrewritten.stargatemechanics.signcoloring;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargate.api.container.Holder;
import org.sgrewritten.stargate.api.network.portal.formatting.LegacyStargateComponent;
import org.sgrewritten.stargate.api.network.portal.formatting.SignLine;
import org.sgrewritten.stargate.api.network.portal.formatting.StargateComponent;

import java.util.List;

public class ColorOverrideFormatter {

    private static void changeColorOfComponent(Holder<StargateComponent> component, ChatColor color) {
        component.value = new LegacyStargateComponent(color + component.value.plainText());
    }

    public static void formatFromOverride(@Nullable ColorOverride colorOverride, SignLine line) {
        if (colorOverride == null) {
            return;
        }
        List<Holder<StargateComponent>> components = line.getComponents();
        if (components.size() == 3) {
            changeColorOfComponent(components.get(0), colorOverride.pointerColor());
            changeColorOfComponent(components.get(1), colorOverride.textColor());
            changeColorOfComponent(components.get(2), colorOverride.pointerColor());
        } else {
            for (Holder<StargateComponent> component : components) {
                changeColorOfComponent(component, colorOverride.textColor());
            }
        }
    }
}
