package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("SameReturnValue")
public interface MItem {
    Identifier[] EMPTY = new Identifier[0];

    default boolean mialib$shouldSmelt(World world, BlockState state, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack) {
        return false;
    }

    default boolean mialib$attack(World world, ItemStack stack, LivingEntity attacker, Entity target) {
        return false;
    }

    default void mialib$killEntity(World world, ItemStack stack, LivingEntity user, LivingEntity victim) {}

    default Identifier[] mialib$cooldownDisplays() {
        return EMPTY;
    }

    default int mialib$enchantLevel(Enchantment enchantment, ItemStack stack, int level) {
        return level;
    }

    default ActionResult mialib$checkEnchantment(EnchantmentTarget target, Enchantment enchantment) {
        return ActionResult.PASS;
    }

    default int mialib$getNameColor(ItemStack stack) {
        return -1;
    }
}