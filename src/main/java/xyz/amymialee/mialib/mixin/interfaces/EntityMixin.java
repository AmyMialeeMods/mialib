package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;
import xyz.amymialee.mialib.util.interfaces.MEntity;

@Mixin(Entity.class)
public abstract class EntityMixin implements MEntity {
    @Override
    public boolean mialib$isIndestructible() {
        return ExtraFlagsComponent.KEY.get(this).isIndestructible();
    }

    @Override
    public boolean mialib$isImmortal() {
        return ExtraFlagsComponent.KEY.get(this).isImmortal();
    }
}