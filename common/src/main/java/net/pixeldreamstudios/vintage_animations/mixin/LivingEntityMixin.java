package net.pixeldreamstudios.vintage_animations.mixin;

import dev.architectury.platform.Platform;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pixeldreamstudios.vintage_animations.IAnimatedPlayer;
import net.pixeldreamstudios.vintage_animations.VintageAnimations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private ModifierLayer<IAnimation> vintageAnimations$animationContainer;
    @Unique
    private int vintageAnimations$ctr = 0;
    @Unique
    private boolean vintageAnimations$switchedMainHandLeft = false;
    @Unique
    private boolean vintageAnimations$switchedMainHandRight = false;

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;)V", at = @At("HEAD"))
    private void playAnimation(InteractionHand interactionHand, CallbackInfo ci) {
        LivingEntity player = (LivingEntity) (Object) this;
        if (player instanceof Player) {
            if (player.level().isClientSide()) {
                ItemStack itemStack = player.getItemInHand(interactionHand);
                if (itemStack.is(ItemTags.AXES) && VintageAnimations.config.chopAnimation) playAnim(player, "chop");
                else if (itemStack.is(ItemTags.PICKAXES) && VintageAnimations.config.pickAnimation)
                    playAnim(player, "pick");
                else if (itemStack.is(ItemTags.SHOVELS) && VintageAnimations.config.digAnimation)
                    playAnim(player, "dig");
                else if (itemStack.is(ItemTags.HOES) && VintageAnimations.config.tillAnimation)
                    playAnim(player, "till");
            }
        }
    }

    @Inject(method = "getAttackAnim", at = @At("RETURN"), cancellable = true)
    private void cancelVanillaAttackAnim(float f, CallbackInfoReturnable<Float> cir) {
        LivingEntity player = (LivingEntity) (Object) this;
        if (player instanceof Player)
            if (player.level().isClientSide())
                if (vintageAnimations$animationContainer != null)
                    if (vintageAnimations$animationContainer.getAnimation() != null)
                        if (vintageAnimations$animationContainer.getAnimation().isActive())
                            cir.setReturnValue(0.0f);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void incrCtr(CallbackInfo ci) {
        LivingEntity player = (LivingEntity) (Object) this;
        if (player instanceof Player) {
            if (player.level().isClientSide()) {
                if (vintageAnimations$animationContainer != null) {
                    boolean vintageAnimations$mainHandLeft = Minecraft.getInstance().options.mainHand().get().equals(HumanoidArm.LEFT);
                    if (vintageAnimations$mainHandLeft && !vintageAnimations$switchedMainHandLeft) {
                        vintageAnimations$animationContainer.addModifier(new MirrorModifier(true), 0);
                        vintageAnimations$switchedMainHandLeft = true;
                        vintageAnimations$switchedMainHandRight = false;
                    }
                    else if (!vintageAnimations$mainHandLeft && !vintageAnimations$switchedMainHandRight) {
                        vintageAnimations$animationContainer.removeModifier(0);
                        vintageAnimations$switchedMainHandRight = true;
                        vintageAnimations$switchedMainHandLeft = false;
                    }
                }
                vintageAnimations$ctr++;
                // don't let it get too big
                if (vintageAnimations$ctr >= 10000) vintageAnimations$ctr = 0;
            }
        }
    }

    private void playAnim(LivingEntity player, String animName) {
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new ResourceLocation(VintageAnimations.MOD_ID, animName));
        KeyframeAnimationPlayer animPlayer = new KeyframeAnimationPlayer(anim)
                .setFirstPersonConfiguration(new FirstPersonConfiguration(
                        VintageAnimations.config.showArmsInFirstPerson,
                        VintageAnimations.config.showArmsInFirstPerson && VintageAnimations.config.showOffHandInFirstPerson,
                        true,
                        VintageAnimations.config.showOffHandInFirstPerson
                ));
        compatCheck(animPlayer);
        vintageAnimations$animationContainer = ((IAnimatedPlayer) player).vintage_animations_getModAnimation();
        if (vintageAnimations$ctr >= anim.endTick) {
            vintageAnimations$animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTEXPO), animPlayer, true);
            vintageAnimations$ctr = 0;
        }
    }

    private void compatCheck(KeyframeAnimationPlayer animPlayer) {
        if (Platform.isModLoaded("firstperson") || Platform.isModLoaded("realcamera")) {
            animPlayer.setFirstPersonMode(FirstPersonMode.DISABLED);
        } else {
            animPlayer.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
        }
    }
}
