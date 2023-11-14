package xyz.amymialee.mialib.mixin;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.util.MRepeatSupplier;

import java.util.function.Supplier;

@Mixin(ItemGroup.class)
public class ItemGroupMixin {
    @Shadow @Final private Supplier<ItemStack> iconSupplier;

    @Inject(method = "getIcon", at = @At("HEAD"), cancellable = true)
    private void mialib$constantIcon(CallbackInfoReturnable<ItemStack> cir) {
        if (this.iconSupplier instanceof MRepeatSupplier<ItemStack> supplier) {
            cir.setReturnValue(supplier.get());
        }
    }
}