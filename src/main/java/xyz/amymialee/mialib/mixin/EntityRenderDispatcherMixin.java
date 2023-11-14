package xyz.amymialee.mialib.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
	@Shadow private boolean renderHitboxes;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getEntityShadows()Lnet/minecraft/client/option/SimpleOption;"))
	private <E extends Entity> void mialib$rayHit(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
//		if (!(this.renderHitboxes && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo())) {
//			if (MiaLibClient.raycastedEntities.contains(entity)) {
//				EntityRenderDispatcher.renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, tickDelta);
//			}
//		}
	}
}