package xyz.amymialee.mialib.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
	@Shadow protected abstract void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm);

	@Inject(method = "renderFirstPersonItem", at = @At(value = "HEAD"), cancellable = true)
	private void mialib$hideItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, @NotNull ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (item.getItem().mialib$shouldHideInHand(player, hand, item)) {
			var mainHand = hand == Hand.MAIN_HAND;
			var arm = mainHand ? player.getMainArm() : player.getMainArm().getOpposite();
			this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
			ci.cancel();
		}
	}
}