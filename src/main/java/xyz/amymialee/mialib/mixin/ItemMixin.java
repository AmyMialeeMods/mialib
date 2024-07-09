package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public class ItemMixin {
    @Mixin(Item.Settings.class)
    private static class ItemSettingsMixin {
//        @WrapWithCondition(method = "maxDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item$Settings;component(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Lnet/minecraft/item/Item$Settings;", ordinal = 0))
//        private <T> boolean mialib$noDurability1(Item.Settings instance, ComponentType<T> type, T value) {
//            if (value instanceof Integer i) {
//                return i != 0;
//            }
//            return true;
//        }
//
//        @WrapWithCondition(method = "maxDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item$Settings;component(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Lnet/minecraft/item/Item$Settings;", ordinal = 2))
//        private <T> boolean mialib$noDurability2(Item.Settings instance, ComponentType<T> type, T value) {
//            if (value instanceof Integer i) {
//                return i != 0;
//            }
//            return true;
//        }
    }
}
