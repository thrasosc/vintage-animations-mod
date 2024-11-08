package net.pixeldreamstudios.vintage_animations.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.pixeldreamstudios.vintage_animations.IAnimatedPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin implements IAnimatedPlayer {
    //Unique annotation will rename private methods/fields if needed to avoid collisions.
    @Unique
    private final ModifierLayer<IAnimation> modAnimationContainer = new ModifierLayer<>();

    /**
     * Add the animation registration to the end of the constructor
     * Or you can use {@link dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess#REGISTER_ANIMATION_EVENT} event for this
     */
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void init(ClientLevel world, GameProfile profile, CallbackInfo ci) {
        //Mixin does not know (yet) that this will be merged with AbstractClientPlayerEntity
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayer) (Object)this).addAnimLayer(1000, modAnimationContainer); //Register the layer with a priority
        //The priority will tell, how important is this animation compared to other mods. Higher number means higher priority
        //Mods with higher priority will override the lower priority mods (if they want to animation anything)
    }

    /**
     * Override the interface function, so we can use it in the future
     */
    @Override
    public ModifierLayer<IAnimation> vintage_animations_getModAnimation() {
        return modAnimationContainer;
    }

}
