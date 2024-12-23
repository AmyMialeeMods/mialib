package xyz.amymialee.mialib.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Inject(method = "drawItemBar", at = @At(value = "TAIL"))
    public void mialib$custombar(ItemStack stack, int x, int y, CallbackInfo ci) {
        var clientPlayerEntity = MinecraftClient.getInstance().player;
        if (clientPlayerEntity != null) stack.getItem().mialib$renderCustomBar((DrawContext) (Object) this, stack, x, y);
    }
}