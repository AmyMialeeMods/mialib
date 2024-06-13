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
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.MiaLib;

@Mixin(MultiplayerServerListWidget.class)
public class MultiplayerServerListWidgetMixin extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {
    @Unique private static final Text MIALIB_SERVERS_TEXT = Text.translatable("%s.servers".formatted(MiaLib.MOD_ID));
    @Unique private static MultiplayerServerListWidget cache;
    @Unique private static boolean spacerAdded;

    public MultiplayerServerListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    @Inject(method = "updateEntries", at = @At(value = "HEAD"))
    private void mialib$cacheWidget(CallbackInfo ci) {
        spacerAdded = false;
        cache = (MultiplayerServerListWidget) (Object) this;
    }

    @Inject(method = "method_36889", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;addEntry(Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I", shift = At.Shift.BEFORE))
    private static <E extends Entry<E>> void mialib$addSpacer(MultiplayerServerListWidget multiplayerServerListWidget, EntryListWidget.Entry<E> server, CallbackInfo ci) {
        if (!spacerAdded && server instanceof MultiplayerServerListWidget.ServerEntry serverEntry) {
            if (multiplayerServerListWidget.screen.getServerList().mialib$getMialibServers().contains(serverEntry.getServer())) {
                spacerAdded = true;
                cache.addEntry(new MultiplayerServerListWidget.Entry() {
                    private final MinecraftClient client = MinecraftClient.getInstance();

                    @Override
                    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                        var i = y + entryHeight / 2 - 9 / 2;
                        if (this.client.currentScreen != null) {
                            context.drawText(
                                    this.client.textRenderer,
                                    MIALIB_SERVERS_TEXT,
                                    this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(MIALIB_SERVERS_TEXT) / 2,
                                    i,
                                    16777215,
                                    false
                            );
                            var string = "-< --- >-";
                            context.drawText(this.client.textRenderer, string, this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(string) / 2, i + 9, 8421504, false);
                        }
                    }

                    @Override
                    public Text getNarration() {
                        return MIALIB_SERVERS_TEXT;
                    }
                });
            }
        }
    }

    @Mixin(MultiplayerServerListWidget.ServerEntry.class)
    private abstract static class ServerEntryMixin {
        @Shadow @Final private ServerInfo server;
        @Shadow @Final private MultiplayerScreen screen;
        @Shadow @Final private MinecraftClient client;
        @Shadow protected abstract void swapEntries(int i, int j);

        @Inject(method = "render", at = @At(value = "TAIL", target = "Lnet/minecraft/client/option/GameOptions;getTouchscreen()Lnet/minecraft/client/option/SimpleOption;"))
        private void mialib$mialibServerMoveIcons(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
            if (this.client.options.getTouchscreen().getValue() || hovered) {
                if (this.screen.getServerList().mialib$getMialibServers().contains(this.server)) {
                    var o = mouseX - x;
                    var p = mouseY - y;
                    if (index > this.screen.getServerList().servers.size() + 1) {
                        if (o < 16 && p < 16) {
                            context.drawTexture(MultiplayerServerListWidget.SERVER_SELECTION_TEXTURE, x, y, 96.0F, 32.0F, 32, 32, 256, 256);
                        } else {
                            context.drawTexture(MultiplayerServerListWidget.SERVER_SELECTION_TEXTURE, x, y, 96.0F, 0.0F, 32, 32, 256, 256);
                        }
                    }
                    if (index < this.screen.getServerList().size()) {
                        if (o < 16 && p > 16) {
                            context.drawTexture(MultiplayerServerListWidget.SERVER_SELECTION_TEXTURE, x, y, 64.0F, 32.0F, 32, 32, 256, 256);
                        } else {
                            context.drawTexture(MultiplayerServerListWidget.SERVER_SELECTION_TEXTURE, x, y, 64.0F, 0.0F, 32, 32, 256, 256);
                        }
                    }
                }
            }
        }

        @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/ServerList;size()I"))
        private int mialib$mialibServerMoveIconRemoval1(@NotNull ServerList instance, Operation<Integer> original) {
            return instance.servers.size();
        }

        @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V", ordinal = 3))
        private boolean mialib$mialibServerMoveIconRemoval1(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
            return !this.screen.getServerList().mialib$getMialibServers().contains(this.server);
        }

        @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V", ordinal = 4))
        private boolean mialib$mialibServerMoveIconRemoval2(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
            return !this.screen.getServerList().mialib$getMialibServers().contains(this.server);
        }

        @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V", ordinal = 5))
        private boolean mialib$mialibServerMoveIconRemoval3(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
            return !this.screen.getServerList().mialib$getMialibServers().contains(this.server);
        }

        @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V", ordinal = 6))
        private boolean mialib$mialibServerMoveIconRemoval4(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
            return !this.screen.getServerList().mialib$getMialibServers().contains(this.server);
        }

        @SuppressWarnings("UnreachableCode")
        @Inject(method = "mouseClicked", at = @At(value = "HEAD"), cancellable = true)
        private void mialib$mialibServerMoveAction(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
            if (this.screen.getServerList().mialib$getMialibServers().contains(this.server)) {
                var this2 = (MultiplayerServerListWidget.ServerEntry) (Object) this;
                var d = mouseX - (double)this.screen.serverListWidget.getRowLeft();
                var e = mouseY - (double)this.screen.serverListWidget.getRowTop(this.screen.serverListWidget.children().indexOf(this2));
                if (d <= 32.0) {
                    var i = this.screen.serverListWidget.children().indexOf(this2);
                    if (d < 16.0 && e < 16.0 && i > this.screen.getServerList().servers.size() + 1) {
                        this.swapEntries(i, i - 1);
                        cir.setReturnValue(true);
                    }
                    if (d < 16.0 && e > 16.0 && i < this.screen.getServerList().size()) {
                        this.swapEntries(i, i + 1);
                        cir.setReturnValue(true);
                    }
                }
            }
        }

        @WrapWithCondition(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget$ServerEntry;swapEntries(II)V"))
        private boolean mialib$mialibServerMoveActionRemoval(MultiplayerServerListWidget.ServerEntry instance, int i, int j) {
            return !this.screen.getServerList().mialib$getMialibServers().contains(this.server);
        }
    }
}