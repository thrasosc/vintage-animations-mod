package net.pixeldreamstudios.vintage_animations;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = VintageAnimations.MOD_ID)
public class VintageAnimConfig implements ConfigData {
    @ConfigEntry.Category("animations")
    @ConfigEntry.Gui.Tooltip
    public boolean chopAnimation = true;
    @ConfigEntry.Category("animations")
    @ConfigEntry.Gui.Tooltip
    public boolean pickAnimation = true;
    @ConfigEntry.Category("animations")
    @ConfigEntry.Gui.Tooltip
    public boolean digAnimation = true;
    @ConfigEntry.Category("animations")
    @ConfigEntry.Gui.Tooltip
    public boolean tillAnimation = true;

    @ConfigEntry.Category("miscellaneous")
    @ConfigEntry.Gui.Tooltip
    public boolean showArmsInFirstPerson = false;
    @ConfigEntry.Category("miscellaneous")
    @ConfigEntry.Gui.Tooltip
    public boolean showOffHandInFirstPerson = true;
}
