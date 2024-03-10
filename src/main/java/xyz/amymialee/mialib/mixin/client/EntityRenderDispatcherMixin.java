package xyz.amymialee.mialib.mixin.client;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.MiaLib;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void mialib$imperceptible(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (MiaLib.EXTRA_FLAGS.get(entity).isImperceptible()) cir.setReturnValue(false);
    }
}