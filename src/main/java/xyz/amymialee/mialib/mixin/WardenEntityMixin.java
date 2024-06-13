package xyz.amymialee.mialib.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.WardenEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WardenEntity.class)
public class WardenEntityMixin {
    @Inject(method = "isValidTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/WardenEntity;isTeammate(Lnet/minecraft/entity/Entity;)Z"), cancellable = true)
    private void mialib$imperceptible(@NotNull Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.mialib$isImperceptible()) cir.setReturnValue(false);
    }
}