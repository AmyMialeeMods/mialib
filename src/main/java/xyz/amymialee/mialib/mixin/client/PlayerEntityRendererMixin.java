package xyz.amymialee.mialib.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void mialib$hideOrPose(@NotNull AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        var stack = player.getStackInHand(hand);
        if (stack.getItem().mialib$shouldHideInHand(player, hand, stack)) {
            cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
            return;
        }
        var pose = stack.getItem().mialib$pose(player, hand, stack);
        if (pose != null) {
            cir.setReturnValue(pose);
        }
    }
}