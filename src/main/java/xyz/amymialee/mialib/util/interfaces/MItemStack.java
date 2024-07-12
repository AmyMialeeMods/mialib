package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface MItemStack {
	default ItemStack mialib$enchantStack(RegistryEntry<Enchantment> enchantment) {
		return this.mialib$enchantStack(enchantment, 1);
	}

	default ItemStack mialib$enchantStack(RegistryEntry<Enchantment> enchantment, int level) {
		return ItemStack.EMPTY;
	}

	default ItemStack mialib$enchantStack(EnchantmentLevelEntry ... enchantmentLevelEntry) {
		return ItemStack.EMPTY;
	}

	default <T> ItemStack mialib$set(ComponentType<? super T> type, @Nullable T value) {
		return ItemStack.EMPTY;
	}

	static @NotNull ItemStack enchantStack(@NotNull ItemStack stack, RegistryEntry<Enchantment> enchantment) {
		return enchantStack(stack, enchantment, 1);
	}

	static @NotNull ItemStack enchantStack(@NotNull ItemStack stack, RegistryEntry<Enchantment> enchantment, int level) {
		stack.addEnchantment(enchantment, level);
		return stack;
	}

	static @NotNull ItemStack enchantStack(@NotNull ItemStack stack, @NotNull EnchantmentLevelEntry @NotNull ... enchantmentLevelEntry) {
		for (var entry : enchantmentLevelEntry) {
			stack.addEnchantment(entry.enchantment, entry.level);
		}
		return stack;
	}

	static <T> @NotNull ItemStack set(@NotNull ItemStack stack, ComponentType<? super T> type, @Nullable T value) {
		stack.set(type, value);
		return stack;
	}
}