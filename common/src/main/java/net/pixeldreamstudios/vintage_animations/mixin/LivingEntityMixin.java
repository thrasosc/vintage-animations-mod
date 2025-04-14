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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pixeldreamstudios.vintage_animations.IAnimatedPlayer;
import net.pixeldreamstudios.vintage_animations.ToolAnimationManager;
import net.pixeldreamstudios.vintage_animations.VintageAnimations;
import net.pixeldreamstudios.vintage_animations.compat.FirstPersonModelCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
  @Unique private ModifierLayer<IAnimation> vintageAnimations$animationContainer;
  @Unique private boolean vintageAnimations$switchedMainHandLeft = false;
  @Unique private boolean vintageAnimations$switchedMainHandRight = true;
  @Unique private String vintageAnimations$currentAnimation = null;
  @Unique private long vintageAnimations$lastAnimationTime = 0L;

  @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;)V", at = @At("HEAD"))
  private void playAnimation(InteractionHand interactionHand, CallbackInfo ci) {
    LivingEntity entity = (LivingEntity) (Object) this;
    if (!(entity instanceof Player player) || !player.level().isClientSide()) {
      return;
    }

    // Skip if animation is already playing and not finished
    if (isAnimationActive() && !shouldAllowNewAnimation()) {
      return;
    }

    ItemStack itemStack = player.getItemInHand(interactionHand);
    ToolAnimationManager.getAnimationFor(itemStack)
        .ifPresent(
            animId -> {
              String animName = animId.getPath();
              playAnim(player, animName);
              vintageAnimations$currentAnimation = animName;
              vintageAnimations$lastAnimationTime = System.currentTimeMillis();
            });
  }

  @Unique
  private boolean isAnimationActive() {
    return vintageAnimations$animationContainer != null
        && vintageAnimations$animationContainer.getAnimation() != null
        && vintageAnimations$animationContainer.getAnimation().isActive();
  }

  @Unique
  private boolean shouldAllowNewAnimation() {
    if (vintageAnimations$currentAnimation == null) {
      return true;
    }

    // Get the animation duration to prevent interruptions
    KeyframeAnimation anim = getAnimationByName(vintageAnimations$currentAnimation);
    if (anim == null) {
      return true;
    }

    // Calculate if enough time has passed to allow a new animation
    // (either animation completed or close to completion)
    long currentTime = System.currentTimeMillis();
    long animDurationMs = anim.endTick * 50L; // Convert ticks to milliseconds
    long elapsedTime = currentTime - vintageAnimations$lastAnimationTime;

    // Allow new animation if at least 90% of the current animation has played
    return elapsedTime >= (animDurationMs * 0.9);
  }

  @Unique
  private KeyframeAnimation getAnimationByName(String animName) {
    ResourceLocation animLocation =
        ResourceLocation.fromNamespaceAndPath(VintageAnimations.MOD_ID, animName);
    return (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(animLocation);
  }

  @Inject(method = "getAttackAnim", at = @At("RETURN"), cancellable = true)
  private void cancelVanillaAttackAnim(float f, CallbackInfoReturnable<Float> cir) {
    LivingEntity entity = (LivingEntity) (Object) this;
    if (!(entity instanceof Player)) {
      return;
    }

    if (entity.level().isClientSide() && isAnimationActive()) {
      cir.setReturnValue(0.0f);
    }
  }

  @Inject(method = "tick", at = @At("RETURN"))
  private void updateAnimationState(CallbackInfo ci) {
    LivingEntity entity = (LivingEntity) (Object) this;
    if (!(entity instanceof Player) || !entity.level().isClientSide()) {
      return;
    }

    if (vintageAnimations$animationContainer != null) {
      boolean mainHandLeft =
          Minecraft.getInstance().options.mainHand().get().equals(HumanoidArm.LEFT);

      // Update animation mirroring based on main hand setting
      if (mainHandLeft && !vintageAnimations$switchedMainHandLeft) {
        vintageAnimations$animationContainer.addModifier(new MirrorModifier(true), 0);
        vintageAnimations$switchedMainHandLeft = true;
        vintageAnimations$switchedMainHandRight = false;
      } else if (!mainHandLeft && !vintageAnimations$switchedMainHandRight) {
        vintageAnimations$animationContainer.removeModifier(0);
        vintageAnimations$switchedMainHandRight = true;
        vintageAnimations$switchedMainHandLeft = false;
      }

      // Reset animation tracking if animation is no longer active
      if (!isAnimationActive()) {
        vintageAnimations$currentAnimation = null;
      }
    }
  }

  @Unique
  private void playAnim(LivingEntity player, String animName) {
    ResourceLocation animLocation =
        ResourceLocation.fromNamespaceAndPath(VintageAnimations.MOD_ID, animName);
    KeyframeAnimation anim = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(animLocation);

    if (anim == null) {
      VintageAnimations.LOGGER.warn("Animation not found: {}", animLocation);
      return;
    }

    KeyframeAnimationPlayer animPlayer =
        new KeyframeAnimationPlayer(anim).setFirstPersonConfiguration(createFirstPersonConfig());

    animPlayer.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
    applyCompatibilitySettings(animPlayer);

    vintageAnimations$animationContainer =
        ((IAnimatedPlayer) player).vintage_animations_getModAnimation();

    // Use fade transition for smooth animation blending
    vintageAnimations$animationContainer.replaceAnimationWithFade(
        AbstractFadeModifier.standardFadeIn(5, Ease.INOUTEXPO), animPlayer, true);
  }

  @Unique
  private FirstPersonConfiguration createFirstPersonConfig() {
    return new FirstPersonConfiguration(
        VintageAnimations.config.showArmsInFirstPerson,
        VintageAnimations.config.showArmsInFirstPerson
            && VintageAnimations.config.showOffHandInFirstPerson,
        true,
        VintageAnimations.config.showOffHandInFirstPerson);
  }

  @Unique
  private void applyCompatibilitySettings(KeyframeAnimationPlayer animPlayer) {
    if (Platform.isModLoaded("firstperson")) {
      new FirstPersonModelCompat(animPlayer);
    }

    if (Platform.isModLoaded("realcamera")) {
      animPlayer.setFirstPersonMode(FirstPersonMode.DISABLED);
    }
  }
}
