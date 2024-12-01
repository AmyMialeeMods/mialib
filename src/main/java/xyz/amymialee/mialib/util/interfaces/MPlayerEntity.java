package xyz.amymialee.mialib.util.interfaces;

public @SuppressWarnings("unused") interface MPlayerEntity {
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