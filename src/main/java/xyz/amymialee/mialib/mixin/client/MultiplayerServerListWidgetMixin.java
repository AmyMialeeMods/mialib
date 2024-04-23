package xyz.amymialee.mialib.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
}