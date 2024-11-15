package net.pixeldreamstudios.vintage_animations.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pixeldreamstudios.vintage_animations.VintageAnimations;

@Mod(VintageAnimations.MOD_ID)
public final class VintageAnimationsForge {
    public VintageAnimationsForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        VintageAnimations.initClient();
    }
}
