package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface MItemStack {
	default ItemStack mialib$enchantStack(Enchantment enchantment) {
		return this.mialib$enchantStack(enchantment, 1);
	}

	default ItemStack mialib$enchantStack(Enchantment enchantment, int level) {
		return ItemStack.EMPTY;
	}

	default ItemStack mialib$enchantStack(EnchantmentLevelEntry ... enchantmentLevelEntry) {
		return ItemStack.EMPTY;
	}

	default ItemStack mialib$enchantBook(Enchantment enchantment) {
		return this.mialib$enchantBook(enchantment, 1);
	}

	default ItemStack mialib$enchantBook(Enchantment enchantment, int level) {
		return ItemStack.EMPTY;
	}

	default ItemStack mialib$enchantBook(EnchantmentLevelEntry ... enchantmentLevelEntry) {
		return ItemStack.EMPTY;
	}

	static @NotNull ItemStack enchantStack(@NotNull ItemStack stack, Enchantment enchantment) {
		return enchantStack(stack, enchantment, 1);
	}

	static @NotNull ItemStack enchantStack(@NotNull ItemStack stack, Enchantment enchantment, int level) {
		stack.addEnchantment(enchantment, level);
		return stack;
	}

	static @NotNull ItemStack enchantStack(@NotNull ItemStack stack, @NotNull EnchantmentLevelEntry @NotNull ... enchantmentLevelEntry) {
		for (var entry : enchantmentLevelEntry) {
			stack.addEnchantment(entry.enchantment, entry.level);
		}
		return stack;
	}
}