package xyz.amymialee.mialib.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.MiaLib;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {
    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "jump", at = @At("HEAD"))
    private void mialib$testJump(CallbackInfo ci) {
        System.out.println((this.getWorld().isClient() ? "CLIENT" : "SERVER") + ": " + MiaLib.TEST_FLOAT.getValue() + " " + MiaLib.TEST_DOUBLE.getValue() + " " + MiaLib.TEST_INTEGER.getValue() + " " + MiaLib.TEST_LONG.getValue() + " " + MiaLib.TEST_BOOLEAN.getValue());
    }
}