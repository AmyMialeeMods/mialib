package xyz.amymialee.mialib.mixin;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.config.MiaLibProperties;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    @Inject(method = "createGameVersion", at = @At("TAIL"))
    private static void mialib$initMialibConstants(CallbackInfo ci) {
        MiaLibProperties.loadConfig();
    }
}