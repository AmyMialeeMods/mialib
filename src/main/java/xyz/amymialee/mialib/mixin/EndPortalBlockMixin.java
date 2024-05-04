package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.Mialib;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    @WrapOperation(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;canUsePortals()Z"))
    private boolean mialib$portalToggle(Entity instance, @NotNull Operation<Boolean> original) {
        return original.call(instance) && !Mialib.DISABLE_END_PORTALS.getValue();
    }
}