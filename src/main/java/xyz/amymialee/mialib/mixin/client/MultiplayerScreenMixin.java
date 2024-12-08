package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.client.MialibServerWidget;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {
    @Shadow private ServerList serverList;
    @Shadow public MultiplayerServerListWidget serverListWidget;
    @Shadow protected abstract void removeEntry(boolean confirmedAction);
    @Shadow private ServerInfo selectedEntry;
    @Shadow protected abstract void editEntry(boolean confirmedAction);
    @Shadow private ButtonWidget buttonEdit;
    @Shadow private ButtonWidget buttonDelete;
    @Shadow protected abstract void connect(ServerInfo entry);

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @WrapOperation(method = "method_19916", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void mialib$cacheServerInfo(MinecraftClient instance, Screen screen, Operation<Void> original) {
        if (screen instanceof AddServerScreen addServerScreen) this.serverList.mialib$setEditTarget(addServerScreen.server);
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

    @Inject(method = "method_19914", at = @At("TAIL"))
    private void mialib$canDelete(CallbackInfo ci) {
        var entry = this.serverListWidget.getSelectedOrNull();
        if (!(entry instanceof MialibServerWidget widget)) return;
        var string = widget.getServer().name;
        if (string == null || this.client == null) return;
        Text text = Text.translatable("selectServer.deleteWarning", string);
        this.client.setScreen(new ConfirmScreen(this::removeEntry, Text.translatable("selectServer.deleteQuestion"), text, Text.translatable("selectServer.deleteButton"), ScreenTexts.CANCEL));
    }

    @Inject(method = "method_19915", at = @At("TAIL"))
    private void mialib$canEdit(CallbackInfo ci) {
        var entry = this.serverListWidget.getSelectedOrNull();
        if (!(entry instanceof MialibServerWidget widget)) return;
        var serverInfo = widget.getServer();
        this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, ServerInfo.ServerType.OTHER);
        this.selectedEntry.copyWithSettingsFrom(serverInfo);
        if (this.client == null) return;
        this.client.setScreen(new AddServerScreen(this, this::editEntry, this.selectedEntry));
    }

    @Inject(method = "updateButtonActivationStates", at = @At("TAIL"))
    private void mialib$updateButtonActivationStates(CallbackInfo ci) {
        var entry = this.serverListWidget.getSelectedOrNull();
        if (entry instanceof MialibServerWidget) {
            this.buttonEdit.active = true;
            this.buttonDelete.active = true;
        }
    }

    @Inject(method = "connect()V", at = @At("TAIL"))
    private void mialib$connect(CallbackInfo ci) {
        var entry = this.serverListWidget.getSelectedOrNull();
        if (entry instanceof MialibServerWidget widget) {
            this.connect(widget.getServer());
        }
    }

    @Inject(method = "removeEntry", at = @At("TAIL"))
    private void mialib$remove(boolean confirmedAction, CallbackInfo ci) {
        var entry = this.serverListWidget.getSelectedOrNull();
        if (confirmedAction && entry instanceof MialibServerWidget widget) {
            this.serverList.remove(widget.getServer());
            this.serverList.saveFile();
            this.serverListWidget.setSelected(null);
            this.serverListWidget.setServers(this.serverList);
        }
    }

    @Inject(method = "editEntry", at = @At("TAIL"))
    private void mialib$edit(boolean confirmedAction, CallbackInfo ci) {
        var entry = this.serverListWidget.getSelectedOrNull();
        if (confirmedAction && entry instanceof MialibServerWidget widget) {
            var serverInfo = widget.getServer();
            serverInfo.name = this.selectedEntry.name;
            serverInfo.address = this.selectedEntry.address;
            serverInfo.copyWithSettingsFrom(this.selectedEntry);
            this.serverList.saveFile();
            this.serverListWidget.setServers(this.serverList);
        }
    }
}