package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.interfaces.MEntity;

@Mixin(Entity.class)
public abstract class EntityMixin implements MEntity {
    @Override
    public boolean mialib$isImperceptible() {
        return MiaLib.EXTRA_FLAGS.get(this).isImperceptible();
    }

    @Override
    public boolean mialib$isIndestructible() {
        return MiaLib.EXTRA_FLAGS.get(this).isIndestructible();
    }

    @Override
    public boolean mialib$isImmortal() {
        return MiaLib.EXTRA_FLAGS.get(this).isImmortal();
    }
}