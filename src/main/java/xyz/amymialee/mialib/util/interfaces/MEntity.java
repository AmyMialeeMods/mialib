package xyz.amymialee.mialib.util.interfaces;

public interface MEntity {
    default boolean mialib$isIndestructible() {
        return false;
    }

    default boolean mialib$isImmortal() {
        return false;
    }
}