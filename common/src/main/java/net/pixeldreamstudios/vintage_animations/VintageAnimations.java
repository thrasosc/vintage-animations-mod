package net.pixeldreamstudios.vintage_animations;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import java.util.logging.Logger;

public final class VintageAnimations {
    public static final String MOD_ID = "vintage_animations";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);
    public static VintageAnimConfig config;

    public static void initClient() {
        AutoConfig.register(VintageAnimConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(VintageAnimConfig.class).getConfig();
    }
}
