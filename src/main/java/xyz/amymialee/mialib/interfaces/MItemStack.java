package xyz.amymialee.mialib.interfaces;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface MItemStack {
	ItemStack mialib$enchantStack(Enchantment enchantment, int level);
	ItemStack mialib$enchantStack(EnchantmentLevelEntry enchantmentLevelEntry);

	static @NotNull ItemStack enchantStack(@NotNull ItemStack stack, Enchantment enchantment, int level) {
		stack.addEnchantment(enchantment, level);
		return stack;
	}

	static @NotNull ItemStack enchantStack(@NotNull ItemStack stack, @NotNull EnchantmentLevelEntry enchantmentLevelEntry) {
		stack.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
		return stack;
	}
}