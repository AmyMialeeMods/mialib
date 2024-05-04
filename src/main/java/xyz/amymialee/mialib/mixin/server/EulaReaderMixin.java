package xyz.amymialee.mialib.mixin.server;

import net.minecraft.server.dedicated.EulaReader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.config.MialibProperties;
import xyz.amymialee.mialib.util.MDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Scanner;

@Mixin(EulaReader.class)
public abstract class EulaReaderMixin {
    @Unique private static boolean doubleChecked = false;

    @Shadow protected abstract boolean checkEulaAgreement();
    @Shadow @Final private Path eulaFile;

    @Inject(method = "checkEulaAgreement", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/EulaReader;createEulaFile()V", shift = At.Shift.AFTER), cancellable = true)
    private void mialib$doubleTake(CallbackInfoReturnable<Boolean> cir) {
        if (!doubleChecked) {
            cir.setReturnValue(this.checkEulaAgreement());
            doubleChecked = true;
        }
    }

    @Inject(method = "createEulaFile", at = @At(value = "TAIL"))
    private void mialib$justAsk(CallbackInfo ci) {
        if (!this.checkEulaAgreement()) {
            var accepted = false;
            if (MialibProperties.eulaAccepted.get()) {
                accepted = true;
                Mialib.LOGGER.info("Automatically agreed to the Minecraft EULA (https://aka.ms/MinecraftEULA) using saved value in %s.".formatted(MDir.getMialibPath("%s.yaml".formatted(Mialib.MOD_ID))));
            }
            if (!accepted) {
                Mialib.LOGGER.info("Enter \"true\" to agree to the Minecraft EULA (https://aka.ms/MinecraftEULA).");
                var scanner = new Scanner(System.in);
                var input = scanner.nextLine().toLowerCase();
                accepted = "true".equals(input);
            }
            if (accepted) {
                try (var outputStream = Files.newOutputStream(this.eulaFile)) {
                    var properties = new Properties();
                    properties.setProperty("eula", "true");
                    properties.store(outputStream, "Minecraft EULA (https://aka.ms/MinecraftEULA) accepted using Mialib.");
                } catch (Exception e) {
                    Mialib.LOGGER.warn("Failed to save {}", this.eulaFile, e);
                }
            }
        }
    }
}