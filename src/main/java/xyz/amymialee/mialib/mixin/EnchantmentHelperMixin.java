package xyz.amymialee.mialib.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getLevel", at = @At("RETURN"), cancellable = true)
    private static void mialib$enchantAdditions(Enchantment enchantment, @NotNull ItemStack stack, @NotNull CallbackInfoReturnable<Integer> cir) {
        var level = cir.getReturnValue();
        level = stack.getItem().mialib$enchantLevel(enchantment, stack, level);
        cir.setReturnValue(level);
    }
}