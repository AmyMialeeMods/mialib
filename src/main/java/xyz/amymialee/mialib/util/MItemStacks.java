package xyz.amymialee.mialib.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface MItemStacks {
	static @NotNull ItemStack enchantStack(@NotNull ItemStack stack, Enchantment enchantment, int level) {
		stack.addEnchantment(enchantment, level);
		return stack;
	}
}