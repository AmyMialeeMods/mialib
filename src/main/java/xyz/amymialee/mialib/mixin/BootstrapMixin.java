package xyz.amymialee.mialib.mixin;

import net.minecraft.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.registration.MRegistry;
import xyz.amymialee.mialib.values.MValues;

@Mixin(Bootstrap.class)
public class BootstrapMixin {
    @Inject(method = "initialize", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/atomic/AtomicLong;set(J)V", shift = At.Shift.AFTER))
    private static void mialib$lateRegistry(CallbackInfo ci) {
        MValues.loadConfig();
        MValues.freeze();
    }
}