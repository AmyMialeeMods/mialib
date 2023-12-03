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
    private static void mialib$poses(@NotNull AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        var itemStack = player.getStackInHand(hand);
        var pose = itemStack.getItem().mialib$pose(player, hand, itemStack);
        if (pose != null) {
            cir.setReturnValue(pose);
        }
    }
}