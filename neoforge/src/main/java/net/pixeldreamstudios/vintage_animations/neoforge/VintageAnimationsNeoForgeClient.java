package net.pixeldreamstudios.vintage_animations.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.pixeldreamstudios.vintage_animations.VintageAnimations;

// @Mod(VintageAnimations.MOD_ID)
// public final class VintageAnimationsNeoForge {
//  public VintageAnimationsNeoForge() {
//    FMLEnvironment.dist(Dist.CLIENT, () -> VintageAnimations::initClient);
//  }
// }

@Mod(value = VintageAnimations.MOD_ID, dist = Dist.CLIENT)
public class VintageAnimationsNeoForgeClient {
  public VintageAnimationsNeoForgeClient(IEventBus modBus) {
    VintageAnimations.initClient();
  }
}
