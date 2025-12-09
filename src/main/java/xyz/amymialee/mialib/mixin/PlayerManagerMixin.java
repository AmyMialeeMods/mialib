package xyz.amymialee.mialib.mixin;

import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.mvalues.MVServerManager;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "sendScoreboard", at = @At("RETURN"))
    private void mialib$syncComponents(ServerScoreboard scoreboard, ServerPlayerEntity player, CallbackInfo ci) {
        MVServerManager.syncCallback(player);
    }
}