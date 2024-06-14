package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.events.MiaLibEvents;

import java.util.Iterator;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean blockedByShield(DamageSource source);

    @SuppressWarnings("unused")
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z"), cancellable = true)
    protected void mialib$modifyDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0, argsOnly = true) LocalFloatRef amountLocal) {
        if (this.blockedByShield(source)) {
            return;
        }
        var damage = MiaLibEvents.DAMAGE_INTERACTION.invoker().modifyDamage((LivingEntity) (Object) this, source, amount);
        if (damage.isPresent()) {
            amountLocal.set(damage.get());
        } else {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(method = "clearStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onStatusEffectRemoved(Lnet/minecraft/entity/effect/StatusEffectInstance;)V"))
    private void mialib$unclearable(LivingEntity instance, @NotNull StatusEffectInstance effect, Operation<Void> original, @Share("shouldClear") @NotNull LocalBooleanRef shouldClear) {
        shouldClear.set(effect.getEffectType().value().mialib$shouldBeCleared(instance, effect));
        if (shouldClear.get()) {
            original.call(instance, effect);
        }
    }

    @WrapOperation(method = "clearStatusEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V"))
    private void mialib$unremoveable(Iterator<StatusEffectInstance> instance, Operation<Void> original, @Share("shouldClear") @NotNull LocalBooleanRef shouldClear) {
        if (shouldClear.get()) {
            original.call(instance);
        }
    }
}