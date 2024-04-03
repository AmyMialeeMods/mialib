package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.util.Identifier;

public interface MPlayerEntity {
    default boolean mialib$isCoolingDown(Identifier id) {
        return false;
    }

    default void mialib$setCooldown(Identifier id, int ticks) {}

    default int mialib$getCooldown(Identifier id) {
        return 0;
    }

    default float mialib$getCooldown(Identifier id, float tickDelta) {
        return 0;
    }

    default boolean mialib$holdingAttack() {
        return false;
    }

    default void mialib$setHoldingAttack(boolean attackHeld) {}

    default int mialib$getHoldingAttackTime() {
        return 0;
    }

    default boolean mialib$holdingUse() {
        return false;
    }

    default void mialib$setHoldingUse(boolean useHeld) {}

    default int mialib$getHoldingUseTime() {
        return 0;
    }
}