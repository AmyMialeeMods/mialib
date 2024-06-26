package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import xyz.amymialee.mialib.detonations.Detonation;

@SuppressWarnings("unused")
public interface MWorld {
    default void mialib$detonate(Detonation detonation, Vec3d pos) {}

    default void mialib$detonate(Detonation detonation, Vec3d pos, Entity owner) {}

    default void mialib$detonate(Detonation detonation, Vec3d pos, Entity owner, Entity projectile) {}
}