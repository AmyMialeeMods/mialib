package xyz.amymialee.mialib.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.Mialib;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean isIn(TagKey<Item> tag);
    @Shadow public abstract Item getItem();

    @Inject(method = "isDamageable", at = @At("HEAD"), cancellable = true)
    private void mialib$disableDamageable(CallbackInfoReturnable<Boolean> cir) {
        if (this.isIn(Mialib.UNBREAKABLE)) cir.setReturnValue(false);
    }

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void getName(CallbackInfoReturnable<Text> cir) {
        var color = this.getItem().mialib$getNameColor((ItemStack) (Object) this);
        if (color == -1) return;
        var text = cir.getReturnValue();
        if (text.getStyle().getColor() == null) cir.setReturnValue(text.mialib$withColor(color));
    }
}