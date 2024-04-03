package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.detonations.Detonation;
import xyz.amymialee.mialib.util.interfaces.MWorld;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements MWorld {
    @Override
    public void mialib$detonate(Detonation detonation, Vec3d pos) {
        MiaLib.LOGGER.error("Tried to detonate a detonation on the client!");
    }

    @Override
    public void mialib$detonate(Detonation detonation, Vec3d pos, Entity owner) {
        MiaLib.LOGGER.error("Tried to detonate a detonation on the client!");
    }

    @Override
    public void mialib$detonate(Detonation detonation, Vec3d pos, Entity owner, Entity projectile) {
        MiaLib.LOGGER.error("Tried to detonate a detonation on the client!");
    }
}