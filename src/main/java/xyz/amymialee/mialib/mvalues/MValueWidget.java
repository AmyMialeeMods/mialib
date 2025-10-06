package xyz.amymialee.mialib.mvalues;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.util.runnables.Consumer3;

@Environment(EnvType.CLIENT)
public abstract class MValueWidget<T> extends ClickableWidget {
    public @Environment(EnvType.CLIENT) static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(Mialib.id("widget/button"), Mialib.id("widget/button_disabled"), Mialib.id("widget/button_highlighted"));
    public @Environment(EnvType.CLIENT) static final Identifier SLIDER_TEXTURE = Mialib.id("widget/slider");
    public @Environment(EnvType.CLIENT) static final Identifier SLIDER_HIGHLIGHTED_TEXTURE = Mialib.id("widget/slider_highlighted");
    public @Environment(EnvType.CLIENT) static final Identifier SLIDER_HANDLE_TEXTURE = Mialib.id("widget/slider_handle");
    public @Environment(EnvType.CLIENT) static final Identifier SLIDER_HANDLE_HIGHLIGHTED_TEXTURE = Mialib.id("widget/slider_handle_highlighted");
    public @Environment(EnvType.CLIENT) static final ButtonTextures CLIENT_BUTTON_TEXTURES = new ButtonTextures(Mialib.id("widget/client_button"), Mialib.id("widget/client_button_disabled"), Mialib.id("widget/client_button_highlighted"));
    public @Environment(EnvType.CLIENT) static final Identifier CLIENT_SLIDER_TEXTURE = Mialib.id("widget/client_slider");
    public @Environment(EnvType.CLIENT) static final Identifier CLIENT_SLIDER_HIGHLIGHTED_TEXTURE = Mialib.id("widget/client_slider_highlighted");
    public @Environment(EnvType.CLIENT) static final Identifier CLIENT_SLIDER_HANDLE_TEXTURE = Mialib.id("widget/client_slider_handle");
    public @Environment(EnvType.CLIENT) static final Identifier CLIENT_SLIDER_HANDLE_HIGHLIGHTED_TEXTURE = Mialib.id("widget/client_slider_handle_highlighted");
    public final MValue<T> value;
    public double scroll;
    public double velocity;
    public boolean scissorContains;

    public MValueWidget(int x, int y, @NotNull MValue<T> value) {
        super(x, y, 144, 18, Text.translatable(value.getTranslationKey()));
        this.value = value;
        this.setMessage(this.value.getText());
        this.setTooltip(Tooltip.of(this.value.getDescription()));
    }

    @Override
    public void setTooltip(@Nullable Tooltip tooltip) {
        if (tooltip != null) {
            var name = this.value.id.getNamespace();
            var container = FabricLoader.getInstance().getModContainer(this.value.id.getNamespace());
            if (container.isPresent()) {
                name = container.get().getMetadata().getName();
            }
            var content = tooltip.content;
            content = content.copy().append("\n").append(Text.literal(name).formatted(Formatting.BLUE));
            tooltip = Tooltip.of(content, tooltip.narration);
        }
        super.setTooltip(tooltip);
    }

    @Override
    protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        var client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;
        var scroll = this.scroll + this.velocity * delta;
        this.scissorContains = context.scissorContains(mouseX, mouseY);
        this.hovered = this.scissorContains
                && mouseX >= this.getX()
                && mouseY >= this.getY() - scroll
                && mouseX < this.getX() + this.width
                && mouseY < this.getY() + this.height - scroll;
        var textures = this.value.clientSide ? CLIENT_BUTTON_TEXTURES : BUTTON_TEXTURES;
        context.mialib$drawGuiTexture(RenderPipelines.GUI_TEXTURED, textures.get(true, this.hovered), this.getX(), this.getY(), 18, this.height, 0xFFFFFFFF);
        context.mialib$drawGuiTexture(RenderPipelines.GUI_TEXTURED, textures.get(true, this.hovered), this.getX() + 18, this.getY(), this.width - 18, this.height, 0xFFFFFFFF);
        context.drawItem(this.value.getStack(), this.getX() + 1, this.getY() + 1);
        final Consumer3<Float, Float, Float> moveAndScale = (x, y, s) -> {
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(x, y);
            context.getMatrices().scale(s);
        };
        var scale = 0.7f;
        moveAndScale.accept(this.getX() + this.getWidth() - 3f, this.getY() + this.getHeight() / 2f, scale);
        var valueText = Text.literal(this.value.type.getValueAsString(this.value));
        context.drawText(client.textRenderer, valueText, -client.textRenderer.getWidth(valueText), -client.textRenderer.fontHeight / 2, 0xFFFFFFFF, true);
        context.getMatrices().popMatrix();
        moveAndScale.accept(this.getX() + 21f, this.getY() + this.getHeight() / 2f, scale);
        var wrapLines = client.textRenderer.wrapLines(this.getMessage(), 165 - client.textRenderer.getWidth(valueText));
        for (var i = 0; i < wrapLines.size(); i++) {
            var text = wrapLines.get(i);
            var y = -(wrapLines.size()) * client.textRenderer.fontHeight * .5 + client.textRenderer.fontHeight * i;
            context.drawText(client.textRenderer, text, 0, (int) y, 0xFFFFFFFF, true);
        }
        context.getMatrices().popMatrix();
    }

    @Override
    public abstract void onClick(Click click, boolean doubled);

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.scissorContains && this.active && this.visible
                && mouseX >= this.getX()
                && mouseY >= this.getY() - this.scroll
                && mouseX < this.getX() + this.width
                && mouseY < this.getY() + this.height - this.scroll;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
