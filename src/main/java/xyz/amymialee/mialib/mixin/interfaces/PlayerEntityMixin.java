package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.MiaLib;
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

    @Override
    public float mialib$getCooldown(Identifier id, float tickDelta) {
        var entry = this.mialib$getCooldown(id);
        if (entry != null) {
            float f = (float)(entry.endTick - entry.startTick);
            float g = (float)entry.endTick - ((float)this.tick + tickDelta);
            return MathHelper.clamp(g / f, 0.0F, 1.0F);
        } else {
            return 0.0F;
        }
    }

    @Override
    public boolean mialib$holdingAttack() {
        return MiaLib.HOLDING.get(this).isHolding();
    }

    @Override
    public void miaLib$setHoldingAttack(boolean attackHeld) {
        MiaLib.HOLDING.get(this).setHolding(attackHeld);
    }

    @Override
    public int mialib$getHoldingTime() {
        return MiaLib.HOLDING.get(this).getTick();
    }
}