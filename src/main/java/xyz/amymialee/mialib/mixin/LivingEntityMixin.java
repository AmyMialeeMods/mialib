package xyz.amymialee.mialib.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
	private void miaLib$glow(CallbackInfoReturnable<Boolean> cir) {
//		if (MiaLibClient.raycastedEntities.contains(this)) {
//			cir.setReturnValue(true);
//		}
	}
}