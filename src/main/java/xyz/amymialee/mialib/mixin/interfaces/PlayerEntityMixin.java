package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.interfaces.MPlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements MPlayerEntity {
    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean mialib$isCoolingDown(Identifier id) {
        return MiaLib.ID_COOLDOWN_COMPONENT.get(this).isCoolingDown(id);
    }

    @Override
    public void mialib$setCooldown(Identifier id, int ticks) {
        MiaLib.ID_COOLDOWN_COMPONENT.get(this).setCooldown(id, ticks);
    }

    @Override
    public int mialib$getCooldown(Identifier id) {
        return MiaLib.ID_COOLDOWN_COMPONENT.get(this).getCooldown(id);
    }

    @Override
    public float mialib$getCooldown(Identifier id, float tickDelta) {
        return MiaLib.ID_COOLDOWN_COMPONENT.get(this).getCooldown(id, tickDelta);
    }

    @Override
    public boolean mialib$holdingAttack() {
        return MiaLib.HOLDING.get(this).isAttacking();
    }

    @Override
    public void mialib$setHoldingAttack(boolean attackHeld) {
        MiaLib.HOLDING.get(this).setAttacking(attackHeld);
    }

    @Override
    public int mialib$getHoldingAttackTime() {
        return MiaLib.HOLDING.get(this).getAttackTicks();
    }

    @Override
    public boolean mialib$holdingUse() {
        return MiaLib.HOLDING.get(this).isUsing();
    }

    @Override
    public void mialib$setHoldingUse(boolean useHeld) {
        MiaLib.HOLDING.get(this).setUsing(useHeld);
    }

    @Override
    public int mialib$getHoldingUseTime() {
        return MiaLib.HOLDING.get(this).getUsageTicks();
    }
}