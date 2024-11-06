package net.pixeldreamstudios.vintage_animations;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = VintageAnimations.MOD_ID)
public class VintageAnimConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean showArmsInFirstPerson = false;
    @ConfigEntry.Gui.Tooltip
    public boolean showOffHandInFirstPerson = true;
}
