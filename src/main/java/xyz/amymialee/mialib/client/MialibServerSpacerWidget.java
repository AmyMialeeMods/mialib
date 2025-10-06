package xyz.amymialee.mialib.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import xyz.amymialee.mialib.Mialib;

public class MialibServerSpacerWidget extends MultiplayerServerListWidget.Entry {
    private static final Text MIALIB_SERVERS_TEXT = Text.translatable("%s.servers".formatted(Mialib.MOD_ID));
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        if (this.client.currentScreen == null) return;
        var i = this.getY() + this.getHeight() / 2 - 9 / 2;
        context.drawTextWithShadow(this.client.textRenderer, MIALIB_SERVERS_TEXT, this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(MIALIB_SERVERS_TEXT) / 2, i, Colors.WHITE);
        var string = "-< --- >-";
        context.drawTextWithShadow(this.client.textRenderer, string, this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(string) / 2, i + 9, Colors.GRAY);
    }

    @Override
    public boolean isOfSameType(MultiplayerServerListWidget.Entry entry) {
        return entry instanceof MialibServerSpacerWidget;
    }

    @Override
    public void connect() {}

    public @Override Text getNarration() {
        return MIALIB_SERVERS_TEXT;
    }
}