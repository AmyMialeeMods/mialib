package xyz.amymialee.mialib.mixin;

import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnchantRandomlyLootFunction.class)
public class EnchantRandomlyLootFunctionMixin {
//    @WrapOperation(method = "method_53327(ZLnet/minecraft/item/ItemStack;Lnet/minecraft/registry/entry/RegistryEntry$Reference;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"))
//    private static boolean mialib$itemAllows(Enchantment enchantment, @NotNull ItemStack stack, Operation<Boolean> original) {
//        var result = stack.getItem().mialib$checkEnchantment(enchantment.target, enchantment);
//        if (result != null && result != ActionResult.PASS) {
//            return result == ActionResult.SUCCESS;
//        }
//        return original.call(enchantment, stack);
//    }
}