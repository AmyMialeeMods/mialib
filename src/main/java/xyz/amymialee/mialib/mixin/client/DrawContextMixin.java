package xyz.amymialee.mialib.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.MialibClient;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow public abstract void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color);

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z", shift = At.Shift.BEFORE))
    public void mialib$customBar(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        var clientPlayerEntity = MinecraftClient.getInstance().player;
        if (clientPlayerEntity != null) {
            stack.getItem().mialib$renderCustomBar((DrawContext) (Object) this, renderer, stack, x, y, countLabel);
        }
    }

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;", shift = At.Shift.BEFORE))
    private void mialib$modelCheck(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
        MialibClient.currentMode = ModelTransformationMode.GUI;
    }

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getItemCooldownManager()Lnet/minecraft/entity/player/ItemCooldownManager;"))
    public void mialib$cooldownOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        var clientPlayerEntity = MinecraftClient.getInstance().player;
        if (clientPlayerEntity != null) {
            var array = stack.getItem().mialib$cooldownDisplays();
            var length = array.length;
            for (var i = 0; i < length; i++) {
                var f = clientPlayerEntity.mialib$getCooldown(array[i], 0.0f);
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