package xyz.amymialee.mialib.interfaces;

public interface MItemGroup {
    default boolean mialib$hasConstantIcon() {
        return false;
    }

    default void mialib$setConstantIcon(boolean constantIcon) {}
}