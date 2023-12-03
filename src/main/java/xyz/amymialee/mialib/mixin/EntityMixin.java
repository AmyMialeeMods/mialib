package xyz.amymialee.mialib.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.MiaLib;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isInvulnerable", at = @At("HEAD"), cancellable = true)
    private void mialib$indestructible(CallbackInfoReturnable<Boolean> cir) {
        if (MiaLib.FLAGS.get(this).isIndestructible()) cir.setReturnValue(true);
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void mialib$indestructible(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (MiaLib.FLAGS.get(this).isIndestructible()) cir.setReturnValue(true);
    }

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void mialib$imperceptible(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (MiaLib.FLAGS.get(this).isImperceptible()) cir.setReturnValue(true);
    }
}