package xyz.amymialee.mialib.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    @SuppressWarnings("unused")
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tryAttack", at = @At("HEAD"), cancellable = true)
    public void mialib$customAttacks(@NotNull Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (!target.isAttackable()) return;
        if (target instanceof EnderDragonPart) target = ((EnderDragonPart)target).owner;
        var main = this.getMainHandStack();
        if (main.getItem().mialib$attack(this.getWorld(), main, this, target)) {
            cir.setReturnValue(true);
        }
    }
}