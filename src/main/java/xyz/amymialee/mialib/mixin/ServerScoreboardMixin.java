package xyz.amymialee.mialib.mixin;

import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.mvalues.MValueManager;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void mialib$init(MinecraftServer server, CallbackInfo ci) {
        MValueManager.create(server);
    }
}