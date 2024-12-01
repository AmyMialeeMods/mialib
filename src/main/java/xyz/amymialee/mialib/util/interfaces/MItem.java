package xyz.amymialee.mialib.util.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public @SuppressWarnings("unused") interface MItem {
    default void mialib$killEntity(World world, ItemStack stack, LivingEntity user, LivingEntity victim) {}

    default int mialib$bonusLevels(ItemStack stack, Enchantment enchantment, int level) {
        return level;
    }

    default int mialib$getNameColor(ItemStack stack) {
        return -1;
    }

    @Environment(EnvType.CLIENT)
    default void mialib$renderCustomBar(DrawContext drawContext, ItemStack stack, int x, int y) {}
}