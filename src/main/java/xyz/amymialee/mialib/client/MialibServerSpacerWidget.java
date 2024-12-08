package xyz.amymialee.mialib.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.text.Text;
import xyz.amymialee.mialib.Mialib;

public class MialibServerSpacerWidget extends MultiplayerServerListWidget.Entry {
    private static final Text MIALIB_SERVERS_TEXT = Text.translatable("%s.servers".formatted(Mialib.MOD_ID));
    private final MinecraftClient client = MinecraftClient.getInstance();

    public @Override void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        var i = y + entryHeight / 2 - 9 / 2;
        if (this.client.currentScreen == null) return;
        context.drawText(this.client.textRenderer, MIALIB_SERVERS_TEXT, this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(MIALIB_SERVERS_TEXT) / 2, i, 16777215, false);
        var string = "-< --- >-";
        context.drawText(this.client.textRenderer, string, this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(string) / 2, i + 9, 8421504, false);
    }

    public @Override Text getNarration() {
        return MIALIB_SERVERS_TEXT;
    }
}