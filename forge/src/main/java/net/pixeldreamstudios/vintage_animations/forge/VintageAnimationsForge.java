package net.pixeldreamstudios.vintage_animations.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.pixeldreamstudios.vintage_animations.VintageAnimations;

@Mod(VintageAnimations.MOD_ID)
public final class VintageAnimationsForge {
    public VintageAnimationsForge() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> VintageAnimations::initClient);
    }
}
