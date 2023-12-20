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