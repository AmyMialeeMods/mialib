package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.SleepManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.MiaLib;

@Mixin(SleepManager.class)
public class SleepManagerMixin {
    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z"))
    private boolean mialib$shouldCountForSleepTotal(ServerPlayerEntity instance, @NotNull Operation<Boolean> original) {
        return original.call(instance) || instance.mialib$isImperceptible() || (instance.isCreative() && MiaLib.CREATIVE_NO_SLEEP.getValue());
    }
}