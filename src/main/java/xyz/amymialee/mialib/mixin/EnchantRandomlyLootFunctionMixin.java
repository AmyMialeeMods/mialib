package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantRandomlyLootFunction.class)
public class EnchantRandomlyLootFunctionMixin {
    @WrapOperation(method = "method_26267(ZLnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/Enchantment;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean mialib$itemAllows(Enchantment enchantment, @NotNull ItemStack stack, Operation<Boolean> original) {
        var result = stack.getItem().mialib$checkEnchantment(enchantment.target, enchantment);
        if (result != null && result != ActionResult.PASS) {
            return result == ActionResult.SUCCESS;
        }
        return original.call(enchantment, stack);
    }
}