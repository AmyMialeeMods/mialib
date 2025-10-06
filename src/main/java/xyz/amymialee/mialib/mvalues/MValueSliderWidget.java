package xyz.amymialee.mialib.mvalues;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xyz.amymialee.mialib.util.runnables.Consumer3;

@Environment(EnvType.CLIENT)
public abstract class MValueSliderWidget<T> extends MValueWidget<T> {
    public double sliderValue;
    public boolean sliderFocused;
    public double mouseX;

    public MValueSliderWidget(int x, int y, @NotNull MValue<T> value) {
        super(x, y, value);
        this.resetSliderValue();
    }

    public abstract void resetSliderValue();

    private Identifier getTexture() {
        if (this.value.clientSide)
            return this.isFocused() && !this.sliderFocused ? CLIENT_SLIDER_HIGHLIGHTED_TEXTURE : CLIENT_SLIDER_TEXTURE;
        return this.isFocused() && !this.sliderFocused ? SLIDER_HIGHLIGHTED_TEXTURE : SLIDER_TEXTURE;
    }

    private Identifier getHandleTexture() {
        if (this.value.clientSide)
            return !this.hovered && !this.sliderFocused ? CLIENT_SLIDER_HANDLE_TEXTURE : CLIENT_SLIDER_HANDLE_HIGHLIGHTED_TEXTURE;
        return !this.hovered && !this.sliderFocused ? SLIDER_HANDLE_TEXTURE : SLIDER_HANDLE_HIGHLIGHTED_TEXTURE;
    }

    @Override
    protected MutableText getNarrationMessage() {
        return Text.translatable("gui.narrate.slider", this.getMessage());
    }

    @Override
    public void appendClickableNarrations(@NotNull NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, Text.translatable("narration.slider.usage.focused"));
            } else {
                builder.put(NarrationPart.USAGE, Text.translatable("narration.slider.usage.hovered"));
            }
        }
    }

    @Override
    protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        var client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;
        this.mouseX = mouseX;
        var scroll = this.scroll + this.velocity * delta;
        this.scissorContains = context.scissorContains(mouseX, mouseY);
        this.hovered = this.scissorContains
                && mouseX >= this.getX()
                && mouseY >= this.getY() - scroll
                && mouseX < this.getX() + this.width
                && mouseY < this.getY() + this.height - scroll;
        var textures = this.value.clientSide ? CLIENT_BUTTON_TEXTURES : BUTTON_TEXTURES;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, textures.get(true, this.hovered), this.getX(), this.getY(), 18, this.height, 0xFFFFFFFF);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getTexture(), this.getX() + 18, this.getY(), this.getWidth() - 18, this.getHeight(), ColorHelper.getWhite(this.alpha));
        context.mialib$drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getHandleTexture(), this.getX() + 18 + (int) (this.sliderValue * (double) (this.width - 8 - 18)), this.getY(), 8, this.getHeight(), ColorHelper.getWhite(this.alpha));
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
    public void onClick(Click click, boolean doubled) {
        if (click.x() < this.getX() + 18) {
            this.value.send(this.value.type.defaultValue);
            this.resetSliderValue();
        } else {
            this.setValueFromMouse(click.x());
        }
    }

    @Override
    public void onRelease(Click click) {
        super.playDownSound(MinecraftClient.getInstance().getSoundManager());
        if (this.sliderFocused) this.value.send(this.getValue());
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused && this.mouseX > this.getX() + 18) {
            var guiNavigationType = MinecraftClient.getInstance().getNavigationType();
            if (guiNavigationType == GuiNavigationType.MOUSE || guiNavigationType == GuiNavigationType.KEYBOARD_TAB) {
                this.sliderFocused = true;
            }
        } else {
            this.sliderFocused = false;
        }
    }

    @Override
    public boolean keyPressed(@NotNull KeyInput input) {
        if (input.key() == 257 || input.key() == 32 || input.key() == 335) {
            this.sliderFocused = !this.sliderFocused;
            return true;
        }
        if (this.sliderFocused) {
            var bl = input.key() == GLFW.GLFW_KEY_LEFT;
            if (bl || input.key() == GLFW.GLFW_KEY_RIGHT) {
                var f = bl ? -1.0F : 1.0F;
                this.setValue(this.sliderValue + (double) (f / (float) (this.width - 8)));
                return true;
            }
        }
        return false;
    }

    private void setValueFromMouse(double mouseX) {
        this.setValue((mouseX - (double) (this.getX() + 18 + 4)) / (double) (this.width - 18 - 8));
    }

    private void setValue(double value) {
        var old = this.sliderValue;
        this.sliderValue = MathHelper.clamp(value, 0.0, 1.0);
        if (old != this.sliderValue) {
            this.value.value = this.getValue();
        }
    }

    @Override
    protected void onDrag(Click click, double offsetX, double offsetY) {
        if (!this.sliderFocused) return;
        this.setValueFromMouse(click.x());
        super.onDrag(click, offsetX, offsetY);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    protected abstract T getValue();
}