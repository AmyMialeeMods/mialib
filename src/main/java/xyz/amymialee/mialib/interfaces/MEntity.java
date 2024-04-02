package xyz.amymialee.mialib.interfaces;

public interface MEntity {
    default boolean mialib$isImperceptible() {
        return false;
    }

    default boolean mialib$isIndestructible() {
        return false;
    }

    default boolean mialib$isImmortal() {
        return false;
    }
}