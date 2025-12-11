package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.util.interfaces.MItem;

@Mixin(Item.class)
public class ItemMixin implements MItem {
    @Mixin(Item.Settings.class)
    private static class ItemSettingsMixin {
        @WrapOperation(method = "maxDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item$Settings;component(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Lnet/minecraft/item/Item$Settings;", ordinal = 0))
        private <T> Item.Settings mialib$nomaxdamage(Item.Settings instance, ComponentType<T> type, T value, Operation<Item.Settings> original, int maxDamage) {
            if (maxDamage == 0) return instance;
            return original.call(instance, type, value);
        }

        @WrapOperation(method = "maxDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item$Settings;component(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Lnet/minecraft/item/Item$Settings;", ordinal = 2))
        private <T> Item.Settings mialib$nodamage(Item.Settings instance, ComponentType<T> type, T value, Operation<Item.Settings> original, int maxDamage) {
            if (maxDamage == 0) return instance;
            return original.call(instance, type, value);
        }
    }
}