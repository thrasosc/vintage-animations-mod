package net.pixeldreamstudios.vintage_animations;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;

public interface IAnimatedPlayer {
    /**
     * Use your mod ID in the method name to avoid collisions with other mods
     * @return Mod animation container
     */
    ModifierLayer<IAnimation> vintage_animations_getModAnimation();
}
