package net.pixeldreamstudios.vintage_animations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

public class ToolAnimationManager {
  private static final Map<ResourceLocation, Predicate<ItemStack>> ANIMATION_PREDICATES =
      new HashMap<>();

  static {
    // Register animations with predicates
    registerAnimation(
        "chop", stack -> stack.is(ItemTags.AXES) && VintageAnimations.config.chopAnimation);
    registerAnimation(
        "pick", stack -> stack.is(ItemTags.PICKAXES) && VintageAnimations.config.pickAnimation);
    registerAnimation(
        "dig", stack -> stack.is(ItemTags.SHOVELS) && VintageAnimations.config.digAnimation);
    registerAnimation(
        "till", stack -> stack.is(ItemTags.HOES) && VintageAnimations.config.tillAnimation);
  }

  private static void registerAnimation(String name, Predicate<ItemStack> predicate) {
    ANIMATION_PREDICATES.put(
        ResourceLocation.fromNamespaceAndPath(VintageAnimations.MOD_ID, name), predicate);
  }

  public static Optional<ResourceLocation> getAnimationFor(ItemStack stack) {
    return ANIMATION_PREDICATES.entrySet().stream()
        .filter(entry -> entry.getValue().test(stack))
        .map(Map.Entry::getKey)
        .findFirst();
  }
}
