package xyz.amymialee.mialib.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import xyz.amymialee.mialib.detonations.Detonation;

public interface MWorld {
    default void mialib$detonate(Detonation detonation, Vec3d pos) {}

    default void mialib$detonate(Detonation detonation, Vec3d pos, Entity owner) {}

    default void mialib$detonate(Detonation detonation, Vec3d pos, Entity owner, Entity projectile) {}
}