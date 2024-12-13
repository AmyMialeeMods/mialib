package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.events.MiaLibEvents;
import xyz.amymialee.mialib.modules.ExtrasModule;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isAlwaysInvulnerableTo", at = @At("RETURN"), cancellable = true)
    private void mialib$indestructible(DamageSource damageSource, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        var self = (Entity) (Object) this;
        var result = MiaLibEvents.DAMAGE_PREVENTION.invoker().isInvulnerableTo(self, damageSource);
        if (result) cir.setReturnValue(true);
    }

    @WrapOperation(method = "tickInVoid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;discard()V"))
    protected void mialib$tickInVoid(Entity instance, Operation<Void> original) {
        if (instance instanceof ItemEntity item && item.getStack().isIn(ExtrasModule.UNDESTROYABLE)) return;
        original.call(instance);
    }
}