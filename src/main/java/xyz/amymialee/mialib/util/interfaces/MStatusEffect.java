package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

public interface MStatusEffect {
    @SuppressWarnings("unused")
    default boolean mialib$shouldBeCleared(LivingEntity entity, StatusEffectInstance instance) {
        return true;
    }
}