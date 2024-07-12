package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.util.interfaces.MItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements MItemStack {
    @Override
    public ItemStack mialib$enchantStack(RegistryEntry<Enchantment> enchantment, int level) {
        return MItemStack.enchantStack((ItemStack) (Object) this, enchantment, level);
    }

    @Override
    public ItemStack mialib$enchantStack(@NotNull EnchantmentLevelEntry ... enchantmentLevelEntry) {
        return MItemStack.enchantStack((ItemStack) (Object) this, enchantmentLevelEntry);
    }

    @Override
    public <T> ItemStack mialib$set(ComponentType<? super T> type, @Nullable T value) {
        return MItemStack.set((ItemStack) (Object) this, type, value);
    }
}