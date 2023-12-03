package xyz.amymialee.mialib.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.MiaLib;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean isIn(TagKey<Item> tag);

    @Inject(method = "isDamageable", at = @At("HEAD"), cancellable = true)
    private void mialib$disableDamageable(CallbackInfoReturnable<Boolean> cir) {
        if (this.isIn(MiaLib.UNBREAKABLE)) {
            cir.setReturnValue(false);
        }
    }
}