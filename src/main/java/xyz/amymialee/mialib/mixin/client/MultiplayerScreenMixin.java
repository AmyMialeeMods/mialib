package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Shadow private ServerList serverList;

    @WrapOperation(method = "method_19916", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void mialib$cacheServerInfo(MinecraftClient instance, Screen screen, Operation<Void> original) {
        if (screen instanceof AddServerScreen addServerScreen) {
            this.serverList.mialib$setEditTarget(addServerScreen.server);
        }
        original.call(instance, screen);
    }

    @WrapOperation(method = "method_19915", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ServerInfo;copyWithSettingsFrom(Lnet/minecraft/client/network/ServerInfo;)V"))
    private void mialib$cacheServerInfo(ServerInfo instance, ServerInfo serverInfo, @NotNull Operation<Void> original) {
        this.serverList.mialib$setEditTarget(serverInfo);
        original.call(instance, serverInfo);
    }

    @WrapOperation(method = "editEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget$ServerEntry;getServer()Lnet/minecraft/client/network/ServerInfo;"))
    private ServerInfo mialib$editMialibServer(MultiplayerServerListWidget.ServerEntry instance, @NotNull Operation<ServerInfo> original) {
        var serverInfo = original.call(instance);
        if (this.serverList.mialib$isEditingMialibServer()) {
            if (!this.serverList.mialib$getMialibServers().contains(serverInfo)) {
                this.serverList.remove(serverInfo);
                this.serverList.mialib$addMialibServer(serverInfo);
            }
        } else {
            if (this.serverList.mialib$getMialibServers().contains(serverInfo)) {
                var hidden = this.serverList.hiddenServers.contains(serverInfo);
                this.serverList.remove(serverInfo);
                this.serverList.add(serverInfo, hidden);
            }
        }
        return serverInfo;
    }

    @WrapOperation(method = "addEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/ServerList;add(Lnet/minecraft/client/network/ServerInfo;Z)V"))
    private void mialib$addMialibServer(@NotNull ServerList instance, ServerInfo serverInfo, boolean hidden, Operation<Void> original) {
        if (instance.mialib$isEditingMialibServer()) {
            instance.mialib$addMialibServer(serverInfo);
        } else {
            original.call(instance, serverInfo, hidden);
        }
    }
}