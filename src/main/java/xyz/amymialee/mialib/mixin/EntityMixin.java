package xyz.amymialee.mialib.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.events.MialibEvents;

@Mixin(Entity.class)
public class EntityMixin {
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "isInvulnerableTo", at = @At("RETURN"), cancellable = true)
    private void mialib$indestructible(DamageSource damageSource, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }
        var self = (Entity) (Object) this;
        var result = MialibEvents.DAMAGE_PREVENTION.invoker().isInvulnerableTo(self, damageSource);
        if (result) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void mialib$imperceptible(@NotNull PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player.mialib$isImperceptible()) {
            cir.setReturnValue(true);
        }
    }
}