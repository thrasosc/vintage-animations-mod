package net.pixeldreamstudios.vintage_animations.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.pixeldreamstudios.vintage_animations.VintageAnimations;

public final class VintageAnimationsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VintageAnimations.initClient();
    }
}
