package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.cca.HoldingComponent;
import xyz.amymialee.mialib.cca.IdCooldownComponent;
import xyz.amymialee.mialib.util.interfaces.MPlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements MPlayerEntity {
    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean mialib$isCoolingDown(Identifier id) {
        return IdCooldownComponent.KEY.get(this).isCoolingDown(id);
    }

    @Override
    public void mialib$setCooldown(Identifier id, int ticks) {
        IdCooldownComponent.KEY.get(this).setCooldown(id, ticks);
    }

    @Override
    public int mialib$getCooldown(Identifier id) {
        return IdCooldownComponent.KEY.get(this).getCooldown(id);
    }

    @Override
    public float mialib$getCooldown(Identifier id, float tickDelta) {
        return IdCooldownComponent.KEY.get(this).getCooldown(id, tickDelta);
    }

    @Override
    public boolean mialib$holdingAttack() {
        return HoldingComponent.KEY.get(this).isAttacking();
    }

    @Override
    public void mialib$setHoldingAttack(boolean attackHeld) {
        HoldingComponent.KEY.get(this).setAttacking(attackHeld);
    }

    @Override
    public int mialib$getHoldingAttackTime() {
        return HoldingComponent.KEY.get(this).getAttackTicks();
    }

    @Override
    public boolean mialib$holdingUse() {
        return HoldingComponent.KEY.get(this).isUsing();
    }

    @Override
    public void mialib$setHoldingUse(boolean useHeld) {
        HoldingComponent.KEY.get(this).setUsing(useHeld);
    }

    @Override
    public int mialib$getHoldingUseTime() {
        return HoldingComponent.KEY.get(this).getUsageTicks();
    }
}