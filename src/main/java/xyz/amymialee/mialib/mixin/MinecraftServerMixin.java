package xyz.amymialee.mialib.mixin;

import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.registration.MRegistry;

@Mixin(Main.class)
public class MinecraftServerMixin {
    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/VanillaDataPackProvider;createManager(Lnet/minecraft/world/level/storage/LevelStorage$Session;)Lnet/minecraft/resource/ResourcePackManager;", shift = At.Shift.BEFORE))
    private static void mialib$serverInit(String[] args, CallbackInfo ci) {
        MiaLib.LOGGER.info("Building %d MiaLib Registr%s".formatted(MRegistry.REGISTRIES.size(), MRegistry.REGISTRIES.size() == 1 ? "y" : "ies"));
        MRegistry.REGISTRIES.forEach(MRegistry::build);
    }
}