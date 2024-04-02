package xyz.amymialee.mialib.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/entity/effect/StatusEffects$1")
public class BadOmenStatusEffectMixin {
    @Inject(method = "applyUpdateEffect", at = @At("HEAD"), cancellable = true)
    private void mialib$imperceptibleToPillagers(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if (entity.mialib$isImperceptible()) ci.cancel();
    }
}