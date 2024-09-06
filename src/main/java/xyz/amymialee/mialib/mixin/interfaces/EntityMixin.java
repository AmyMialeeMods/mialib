package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;
import xyz.amymialee.mialib.util.interfaces.MEntity;

@Mixin(Entity.class)
public abstract class EntityMixin implements MEntity {
    @Shadow public abstract Vec3d getPos();
    @Shadow public abstract float getHeight();

    @Override
    public boolean mialib$isIndestructible() {
        return ExtraFlagsComponent.KEY.get(this).isIndestructible();
    }

    @Override
    public boolean mialib$isImmortal() {
        return ExtraFlagsComponent.KEY.get(this).isImmortal();
    }

    @Override
    public Vec3d mialib$getBodyPos(double heightScale) {
        return this.getPos().add(0, this.getHeight() * heightScale, 0);
    }
}