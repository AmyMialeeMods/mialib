package xyz.amymialee.mialib.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.registration.MRegistry;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setFramerateLimit(I)V", shift = At.Shift.BEFORE))
    private void mialib$clientInit(RunArgs args, CallbackInfo ci) {
        MiaLib.LOGGER.info("Building %d MiaLib Registries".formatted(MRegistry.REGISTRIES.size()));
        MRegistry.REGISTRIES.forEach(MRegistry::build);
    }
}