package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.client.MialibServerSpacerWidget;
import xyz.amymialee.mialib.client.MialibServerWidget;

@Mixin(MultiplayerServerListWidget.class)
public class MultiplayerServerListWidgetMixin extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {
    @Shadow @Final public MultiplayerScreen screen;
    @Unique private ServerList servers;

    public MultiplayerServerListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
    }

    @Inject(method = "setServers", at = @At("HEAD"))
    private void mialib$serverlist(ServerList serverList, CallbackInfo ci) {
        this.servers = serverList;
    }

    @WrapOperation(method = "updateEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;addEntry(Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I"))
    private int mialib$mialibservers(@NotNull MultiplayerServerListWidget instance, EntryListWidget.Entry<?> entry, @NotNull Operation<Integer> original) {
        instance.addEntry(new MialibServerSpacerWidget());
        if (this.servers != null) this.servers.mialib$getMialibServers().forEach(server -> this.addEntry(new MialibServerWidget(instance, this.screen, server)));
        return original.call(instance, entry);
    }

//    @Mixin(MultiplayerServerListWidget.ServerEntry.class)
//    private abstract static class ServerEntryMixin {
//        @Shadow @Final private ServerInfo server;
//        @Shadow @Final private MultiplayerScreen screen;
//        @Shadow @Final private MinecraftClient client;
//        @Shadow protected abstract void swapEntries(int i, int j);

//        @Inject(method = "render", at = @At(value = "TAIL", target = "Lnet/minecraft/client/option/GameOptions;getTouchscreen()Lnet/minecraft/client/option/SimpleOption;"))
//        private void mialib$mialibServerMoveIcons(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
//            if (this.client.options.getTouchscreen().getValue() || hovered) {
//                if (this.screen.getServerList().mialib$getMialibServers().contains(this.server)) {
//                    var o = mouseX - x;
//                    var p = mouseY - y;
//                    if (index > this.screen.getServerList().servers.size() + 1) {
//                        if (o < 16 && p < 16) {
//                            context.mialib$drawTexture(MultiplayerServerListWidget.MOVE_UP_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
//                        } else {
//                            context.mialib$drawTexture(MultiplayerServerListWidget.MOVE_UP_TEXTURE, x, y, 32, 32);
//                        }
//                    }
//                    if (index < this.screen.getServerList().size()) {
//                        if (o < 16 && p > 16) {
//                            context.mialib$drawTexture(MultiplayerServerListWidget.MOVE_DOWN_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
//                        } else {
//                            context.mialib$drawTexture(MultiplayerServerListWidget.MOVE_DOWN_TEXTURE, x, y, 32, 32);
//                        }
//                    }
//                }
//            }
//        }

//        @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/ServerList;size()I"))
//        private int mialib$mialibServerMoveIconRemoval1(@NotNull ServerList instance, Operation<Integer> original) {
//            return instance.servers.size();
//        }

//        @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", ordinal = 3))
//        private boolean mialib$mialibServerMoveIconRemoval1(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
//            return !this.screen.getServerList().mialib$getMialibServers().contains(this.server);
//        }

//        @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", ordinal = 4))
//        private boolean mialib$mialibServerMoveIconRemoval2(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
//            return !this.screen.getServerList().mialib$getMialibServers().contains(this.server);
//        }

//        @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", ordinal = 5))
//        private boolean mialib$mialibServerMoveIconRemoval3(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
//            return !this.screen.getServerList().mialib$getMialibServers().contains(this.server);
//        }

//        @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", ordinal = 6))
//        private boolean mialib$mialibServerMoveIconRemoval4(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
//            return !this.screen.getServerList().mialib$getMialibServers().contains(this.server);
//        }

//        @SuppressWarnings("UnreachableCode")
//        @Inject(method = "mouseClicked", at = @At(value = "HEAD"), cancellable = true)
//        private void mialib$mialibServerMoveAction(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
//            if (this.screen.getServerList().mialib$getMialibServers().contains(this.server)) {
//                var this2 = (MultiplayerServerListWidget.ServerEntry) (Object) this;
//                var d = mouseX - (double)this.screen.serverListWidget.getRowLeft();
//                var e = mouseY - (double)this.screen.serverListWidget.getRowTop(this.screen.serverListWidget.children().indexOf(this2));
//                if (d <= 32.0) {
//                    var i = this.screen.serverListWidget.children().indexOf(this2);
//                    if (d < 16.0 && e < 16.0 && i > this.screen.getServerList().servers.size() + 1) {
//                        this.swapEntries(i, i - 1);
//                        cir.setReturnValue(true);
//                    }
//                    if (d < 16.0 && e > 16.0 && i < this.screen.getServerList().size()) {
//                        this.swapEntries(i, i + 1);
//                        cir.setReturnValue(true);
//                    }
//                }
//            }
//        }
//
//        @WrapWithCondition(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget$ServerEntry;swapEntries(II)V"))
//        private boolean mialib$mialibServerMoveActionRemoval(MultiplayerServerListWidget.ServerEntry instance, int i, int j) {
//            return !this.screen.getServerList().mialib$getMialibServers().contains(this.server);
//        }
//    }
}