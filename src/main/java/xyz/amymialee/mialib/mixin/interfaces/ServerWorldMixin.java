package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.detonations.Detonation;
import xyz.amymialee.mialib.util.interfaces.MWorld;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements MWorld {
    @Override
    public void mialib$detonate(@NotNull Detonation detonation, Vec3d pos) {
        detonation.executeDetonation((ServerWorld) (Object) this, pos);
    }

    @Override
    public void mialib$detonate(@NotNull Detonation detonation, Vec3d pos, Entity owner) {
        detonation.executeDetonation((ServerWorld) (Object) this, pos, owner);
    }

    @Override
    public void mialib$detonate(@NotNull Detonation detonation, Vec3d pos, Entity owner, Entity projectile) {
        detonation.executeDetonation((ServerWorld) (Object) this, pos, owner, projectile);
    }
}