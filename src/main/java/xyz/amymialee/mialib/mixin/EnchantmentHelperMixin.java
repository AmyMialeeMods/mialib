package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
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

    @WrapOperation(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAvailableForRandomSelection()Z"))
    private static boolean mialib$storeEnchants(Enchantment enchantment, @NotNull Operation<Boolean> original, @Share("storedEnchantment") @NotNull LocalRef<Enchantment> storedEnchantment) {
        storedEnchantment.set(enchantment);
        return original.call(enchantment);
    }

    @WrapOperation(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean mialib$itemAllows(EnchantmentTarget enchantmentTarget, @NotNull Item item, Operation<Boolean> original, @Share("storedEnchantment") @NotNull LocalRef<Enchantment> storedEnchantment) {
        var stored = storedEnchantment.get();
        var result = item.mialib$checkEnchantment(enchantmentTarget, stored);
        if (result != null && result != ActionResult.PASS) {
            return result == ActionResult.SUCCESS;
        }
        return original.call(enchantmentTarget, item);
    }
}