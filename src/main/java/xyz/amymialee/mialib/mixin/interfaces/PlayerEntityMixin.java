package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.cca.IdCooldownComponent;
import xyz.amymialee.mialib.interfaces.MPlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements MPlayerEntity {
    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean mialib$isCoolingDown(Identifier id) {
        return IdCooldownComponent.isCoolingDown((PlayerEntity) (Object) this, id);
    }

    @Override
    public void mialib$setCooldown(Identifier id, int ticks) {
        IdCooldownComponent.setCooldown((PlayerEntity) (Object) this, id, ticks);
    }

    @Override
    public int mialib$getCooldown(Identifier id) {
        return IdCooldownComponent.getCooldown((PlayerEntity) (Object) this, id);
    }
}