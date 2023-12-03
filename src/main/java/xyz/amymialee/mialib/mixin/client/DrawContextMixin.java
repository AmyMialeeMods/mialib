package xyz.amymialee.mialib.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow public abstract void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color);

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getItemCooldownManager()Lnet/minecraft/entity/player/ItemCooldownManager;"))
    public void mialib$cooldownOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        var clientPlayerEntity = MinecraftClient.getInstance().player;
        if (clientPlayerEntity != null) {
            var array = stack.getItem().mialib$cooldownDisplays();
            var length = array.length;
            for (var i = 0; i < length; i++) {
                var f = clientPlayerEntity.mialib$getCooldown(array[i], this.client.getTickDelta());
                if (f > 0.0F) {
                    var width = (16f / length);
                    var k = y + MathHelper.floor(16.0F * (1.0F - f));
                    var l = k + MathHelper.ceil(16.0F * f);
                    this.fill(RenderLayer.getGuiOverlay(), (int) (x + width * i), k, (int) (x + width * (i + 1)), l, Integer.MAX_VALUE);
                }
            }
        }
    }
}