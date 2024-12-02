package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.util.math.Vec3d;

public interface MEntity {
    default boolean mialib$isIndestructible() {
        return false;
    }

    default boolean mialib$isImmortal() {
        return false;
    }

    default boolean mialib$canFly() {
        return false;
    }

    default Vec3d mialib$getBodyPos(double heightScale) {
        return Vec3d.ZERO;
    }
}