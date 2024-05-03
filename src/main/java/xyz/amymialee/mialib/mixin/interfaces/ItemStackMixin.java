package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.util.interfaces.MItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements MItemStack {
    @Override
    public ItemStack mialib$enchantStack(Enchantment enchantment, int level) {
        return MItemStack.enchantStack((ItemStack) (Object) this, enchantment, level);
    }

    @Override
    public ItemStack mialib$enchantStack(@NotNull EnchantmentLevelEntry ... enchantmentLevelEntry) {
        return MItemStack.enchantStack((ItemStack) (Object) this, enchantmentLevelEntry);
    }
}