package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

@SuppressWarnings("unused")
public interface MStatusEffect {
    @SuppressWarnings("SameReturnValue")
    default boolean mialib$shouldBeCleared(LivingEntity entity, StatusEffectInstance instance) {
        return true;
    }
}