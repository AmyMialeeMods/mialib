package xyz.amymialee.mialib.values.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.values.MValue;

@Environment(EnvType.CLIENT)
public class MValueSlider<T> extends SliderWidget {
    private static final Identifier TEXTURE = new Identifier("textures/gui/sprites/widget/slider.png");
    private static final Identifier HIGHLIGHTED_TEXTURE = new Identifier("textures/gui/sprites/widget/slider_highlighted.png");
    private static final Identifier HANDLE_TEXTURE = new Identifier("textures/gui/sprites/widget/slider_handle.png");
    private static final Identifier HANDLE_HIGHLIGHTED_TEXTURE = new Identifier("textures/gui/sprites/widget/slider_handle_highlighted.png");
    private final MValue<T> mValue;

    public MValueSlider(@NotNull MValue<T> mValue, int x, int y, int width) {
        super(x, y, width, 20, Text.empty(), mValue.getScaledValue());
        this.mValue = mValue;
        this.setMessage(mValue.getValueTextFactory().apply(mValue, mValue.getValue()));
    }

    private Identifier getSliderTexture() {
        return this.isFocused() && !this.sliderFocused ? HIGHLIGHTED_TEXTURE : TEXTURE;
    }

    private Identifier getSliderHandleTexture() {
        return !this.hovered && !this.sliderFocused ? HANDLE_TEXTURE : HANDLE_HIGHLIGHTED_TEXTURE;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.mValue.getValueTextFactory().apply(this.mValue, this.mValue.getValue()));
        this.setTooltip(Tooltip.of(this.mValue.getTooltipFactory().apply(this.mValue, this.mValue.getValue())));
    }

    @Override
    protected void applyValue() {
        this.mValue.setScaledValue(this.value);
        this.mValue.sendValue();
    }

//    @Override
//    public void renderButton(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
//        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
//        final var backgroundHeight = 16;
//        context.drawTexture(this.getSliderTexture(), this.getX(), this.getY() + this.height / 2 - backgroundHeight / 2, 0, 0, this.width / 2, backgroundHeight - 1);
//        context.drawTexture(this.getSliderTexture(), this.getX(), this.getY() + this.height / 2 - backgroundHeight / 2 + backgroundHeight - 1, 0, 0, this.width / 2, 1);
//        context.drawTexture(this.getSliderTexture(), this.getX() + this.width / 2, this.getY() + this.height / 2 - backgroundHeight / 2, 200 - this.width / 2, 0, this.width / 2, backgroundHeight - 1);
//        context.drawTexture(this.getSliderTexture(), this.getX() + this.width / 2, this.getY() + this.height / 2 - backgroundHeight / 2 + backgroundHeight - 1, 200 - this.width / 2, 0, this.width / 2, 1);
//        context.drawGuiTexture(this.getHandleTexture(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight());
//    }
}