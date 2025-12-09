package xyz.amymialee.mialib.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.mvalues.MValueColorWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class ColorPickerOverlayScreen extends Screen {
    final Screen parent;
    final MValueColorWidget source;
    final int defaultValue;
    ColorPickerWidget colorPicker;

    public ColorPickerOverlayScreen(Screen parent, @NotNull MValueColorWidget source, int defaultValue) {
        super(source.value.getText());
        this.parent = parent;
        this.source = source;
        this.defaultValue = defaultValue;
    }

    @Override
    public void clearAndInit()
    {
        super.clearAndInit();
    }

    @Override
    protected void init() {
        super.init();
        this.colorPicker = this.addDrawableChild(new ColorPickerWidget(this.width / 2 - 50, this.height / 2 - 50, this.getTitle(), this.colorPicker == null ? this.source.value.get() : this.colorPicker.value, this.colorPicker != null && this.colorPicker.hsv));
        if (this.client != null) this.parent.init(this.width, this.height);
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("mialib.screen.mvalues.color_picker.confirm"), i -> this.close()).dimensions(this.width / 2 - 95, this.height / 2 + 68, 90, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("mialib.screen.mvalues.color_picker.reset"), i -> this.colorPicker.setValue(this.defaultValue)).dimensions(this.width / 2 + 5, this.height / 2 + 68, 90, 20).build());
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.parent != null) this.parent.render(context, 0, 0, deltaTicks);
        if (this.client != null && this.client.world != null) {
            this.renderDarkening(context);
            this.renderDarkening(context);
        }
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void close() {
        if (this.client == null) return;
        this.client.setScreen(this.parent);
        this.source.value.send(this.colorPicker.value);
    }

    static class ColorPickerWidget extends ClickableWidget {
        static final Identifier TEXTURE = Mialib.id("textures/gui/mvalue/color_picker.png");
        final List<ColorChannelSlider> channels = new ArrayList<>();
        final TextFieldWidget hexInputField;
        final ButtonWidget RGButton, HSButton;
        ColorChannelSlider red, green, blue;
        int value;
        boolean hsv;

        public ColorPickerWidget(int x, int y, Text title, int startColor, boolean hsv) {
            super(x, y, 100, 116, title);
            this.value = startColor;
            this.channels.add(this.red = new ColorChannelSlider(x + 2, y + 36, this.width - 4, 20, ColorHelper.getRedFloat(startColor), new Vector3f(1, 0, 0), this::onColorChanged));
            this.channels.add(this.green = new ColorChannelSlider(x + 2, y + 57, this.width - 4, 20, ColorHelper.getGreenFloat(startColor), new Vector3f(0, 1, 0), this::onColorChanged));
            this.channels.add(this.blue = new ColorChannelSlider(x + 2, y + 78, this.width - 4, 20, ColorHelper.getBlueFloat(startColor), new Vector3f(0, 0, 1), this::onColorChanged));
            this.hexInputField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 60, 20, Text.of("Hex Field"));
            this.hexInputField.setMaxLength(6);
            this.hexInputField.setTextPredicate(i -> {
                for (var c : i.toCharArray()) if (!HexFormat.isHexDigit(c)) return false;
                return true;
            });
            this.setHsv(hsv);
            this.hexInputField.setPosition(x + 3, y + 15);
            this.hexInputField.setChangedListener(this::setColorHex);
            this.RGButton = ButtonWidget.builder(Text.of("RGB"), i -> this.setHsv(false)).dimensions(x + 27 - 3, y + 101 - 3, 25, 17).build();
            this.HSButton = ButtonWidget.builder(Text.of("HSV"), i -> this.setHsv(true)).dimensions(x + 54 - 3, y + 101 - 3, 25, 17).build();
            this.RGButton.setAlpha(0);
            this.HSButton.setAlpha(0);
            this.onColorChanged();
        }

        @Override
        public void setPosition(int x, int y) {
            super.setPosition(x, y);
            this.red.setPosition(x + 2, y + 36);
            this.green.setPosition(x + 2, y + 57);
            this.blue.setPosition(x + 2, y + 78);
            this.hexInputField.setPosition(x + 3, y + 15);
        }

        @Override
        protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
            var matrices = context.getMatrices();
            matrices.pushMatrix();
            int x = this.getX(), y = this.getY();
            matrices.pushMatrix();
            matrices.translate(x - 3, y - 3);
            var tRender = MinecraftClient.getInstance().textRenderer;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, 106, 104, 106, 121);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 27, 101, this.hsv ? 25 : 0, 104, 25, 17, 106, 121);
            context.drawText(tRender, Text.of("RGB"), 31, 106, this.RGButton.isHovered() ? 0xffffffff : 0xff000000, false);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 54, 101, this.hsv ? 0 : 25, 104, 25, 17, 106, 121);
            context.drawText(tRender, Text.of("HSV"), 58, 106, this.HSButton.isHovered() ? 0xffffffff : 0xff000000, false);
            matrices.popMatrix();
            this.RGButton.render(context, mouseX, mouseY, delta);
            this.HSButton.render(context, mouseX, mouseY, delta);
            context.drawText(tRender, this.getMessage(), x + (this.width - tRender.getWidth(this.getMessage())) / 2, y - 14, 0xffffffff, true);
            this.channels.forEach(i -> i.render(context, mouseX, mouseY, delta));
            this.hexInputField.render(context, mouseX, mouseY, delta);
            context.fill(x + this.width - 35, y + 3, x + this.width - 3, y + 3 + 32, ColorHelper.withAlpha(255, this.value));
            matrices.popMatrix();
        }

        @Override
        public void onClick(Click click, boolean doubled) {
            super.onClick(click, doubled);
            this.setFocused(true);
            this.channels.forEach(i -> i.onClick(click, doubled));
            this.hexInputField.setFocused(this.hexInputField.isHovered());
            this.hexInputField.onClick(click, doubled);
            if (this.RGButton.isHovered()) this.RGButton.onClick(click, doubled);
            if (this.HSButton.isHovered()) this.HSButton.onClick(click, doubled);
        }

        @Override
        public boolean keyPressed(KeyInput input) {
            return this.hexInputField.keyPressed(input);
        }

        @Override
        public boolean charTyped(CharInput input) {
            return this.hexInputField.charTyped(input);
        }

        @Override
        protected void onDrag(Click click, double offsetX, double offsetY) {
            super.onDrag(click, offsetX, offsetY);
            this.channels.forEach(i -> i.onDrag(click, offsetX, offsetY));
        }

        @Override
        public void onRelease(Click click) {
            super.onRelease(click);
            this.channels.forEach(i -> i.onRelease(click));
            this.hexInputField.onRelease(click);
        }

        public void setValue(int value) {
            var full = ColorHelper.withAlpha(1f, value);
            this.red.setFullColor(full);
            this.green.setFullColor(full);
            this.blue.setFullColor(full);
            this.hexInputField.setText(Integer.toHexString(ColorHelper.withAlpha(0f, value)));
            this.value = value;
        }

        void onColorChanged() {
            int fullColor;
            if (this.hsv) {
                fullColor = ColorHelper.withAlpha(1f, Color.getHSBColor(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB());
            } else {
                fullColor = ColorHelper.fromFloats(1f, this.red.getValue(), this.green.getValue(), this.blue.getValue());
            }
            this.red.setFullColor(fullColor);
            this.green.setFullColor(fullColor);
            this.blue.setFullColor(fullColor);
            this.value = ColorHelper.withAlpha(0f, fullColor);
            this.hexInputField.setText(Integer.toHexString(fullColor).substring(2));
        }

        void setColorHex(@NotNull String v) {
            if (v.length() != 6) return;
            var color = HexFormat.fromHexDigits(v);
            if (this.value == color) return;
            this.updateSliders(color);
            this.red.setFullColor(color);
            this.green.setFullColor(color);
            this.blue.setFullColor(color);
            this.value = color;
        }

        void updateSliders(int color) {
            if (this.hsv) {
                var hsvFloats = Color.RGBtoHSB(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color), null);
                this.red.setValue(hsvFloats[0]);
                this.green.setValue(hsvFloats[1]);
                this.blue.setValue(hsvFloats[2]);
            } else {
                this.red.setValue(ColorHelper.getRedFloat(color));
                this.green.setValue(ColorHelper.getGreenFloat(color));
                this.blue.setValue(ColorHelper.getBlueFloat(color));
            }
        }

        void setHsv(boolean hsv) {
            this.hsv = hsv;
            this.channels.forEach(i -> i.setHSV(hsv));
            this.updateSliders(this.value);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

        static class ColorChannelSlider extends SliderWidget {
            private static final Identifier HANDLE = Identifier.of("widget/slider_handle");
            private static final Identifier HANDLE_HIGHLIGHTED = Identifier.of("widget/slider_handle_highlighted");
            final Vector3f channel;
            final Runnable onChangeValue;
            int fullColor;
            boolean hsv;

            public ColorChannelSlider(int x, int y, int width, int height, double value, Vector3f channel, Runnable onChangeValue) {
                super(x, y, width, height, Text.empty(), value);
                this.channel = channel;
                this.onChangeValue = onChangeValue;
            }

            @Override
            protected void updateMessage() {}

            @Override
            protected void applyValue()
            {
                this.onChangeValue.run();
            }

            @Override
            public void onClick(Click click, boolean doubled) {
                if (this.isHovered()) super.onClick(click, doubled);
                this.setFocused(this.isHovered());
            }

            @Override
            public void onDrag(Click click, double offsetX, double offsetY) {
                if (this.isFocused()) super.onDrag(click, offsetX, offsetY);
            }

            @Override
            public void onRelease(Click click) {
                super.onRelease(click);
                if (this.isFocused()) this.setFocused(false);
            }

            @Override
            public void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
                var matrices = context.getMatrices();
                matrices.pushMatrix();
                matrices.pushMatrix();
                matrices.rotate((float)Math.toRadians(90));
                this.drawPreviewGradient(context);
                if (this.hsv && this.channel.x > 0f) this.drawHueGradient(context, Math.min(this.alpha * this.channel.x, 1f));
                matrices.popMatrix();
                context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, this.hovered ? 0xffffffff : 0xff000000);
                context.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, this.hovered ? 0xffffffff : 0xff000000);
                context.fill(this.getX(), this.getY() + 1, this.getX() + 1, this.getY() + this.height - 1, this.hovered ? 0xffffffff : 0xff000000);
                context.fill(this.getX() + this.width - 1, this.getY() + 1, this.getX() + this.width, this.getY() + this.height - 1, this.hovered ? 0xffffffff : 0xff000000);
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, !this.hovered && !this.isFocused() ? HANDLE : HANDLE_HIGHLIGHTED, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight(), ColorHelper.getWhite(this.alpha));
                var c = this.active ? 16777215 : 10526880;
                this.drawTextWithMargin(context.getHoverListener(this, DrawContext.HoverType.NONE), this.getMessage().mialib$withColor(c | MathHelper.ceil(this.alpha * 255f) << 24), 2);
                matrices.popMatrix();
            }

            void drawPreviewGradient(@NotNull DrawContext context) {
                var start = this.getPreviewColor(true);
                var end = this.getPreviewColor(false);
                context.fillGradient(this.getY() + 3, -this.getX() - this.width + 1, this.getY() + this.height - 3, -this.getX() - 1, ColorHelper.withAlpha(this.alpha, start), ColorHelper.withAlpha(this.alpha, end));
            }

            void drawHueGradient(@NotNull DrawContext context, float alpha) {
                var hsv = Color.RGBtoHSB(ColorHelper.getRed(this.fullColor), ColorHelper.getGreen(this.fullColor), ColorHelper.getBlue(this.fullColor), null);
                var r = Color.getHSBColor(0, hsv[1], hsv[2]).getRGB();
                var g = Color.getHSBColor(0.66f, hsv[1], hsv[2]).getRGB();
                var b = Color.getHSBColor(0.33f, hsv[1], hsv[2]).getRGB();
                context.fillGradient(this.getY() + 3, -this.getX() - this.width + 1, this.getY() + this.height - 3, -this.getX() - this.width / 3 * 2, ColorHelper.withAlpha(alpha, r), ColorHelper.withAlpha(alpha, g));
                context.fillGradient(this.getY() + 3, -this.getX() - this.width / 3 * 2, this.getY() + this.height - 3, -this.getX() - this.width / 3, ColorHelper.withAlpha(alpha, g), ColorHelper.withAlpha(alpha, b));
                context.fillGradient(this.getY() + 3, -this.getX() - this.width / 3, this.getY() + this.height - 3, -this.getX() - 1, ColorHelper.withAlpha(alpha, b), ColorHelper.withAlpha(alpha, r));
            }

            int getPreviewColor(boolean full) {
                if (this.hsv) {
                    var hsvFloats = Color.RGBtoHSB(ColorHelper.getRed(this.fullColor), ColorHelper.getGreen(this.fullColor), ColorHelper.getBlue(this.fullColor), null);
                    var color = ColorHelper.lerp(full ? 1f : 1f - Math.min(this.channel.y, hsvFloats[2]), 0xffffff, this.fullColor); //sat
                    color = ColorHelper.lerp(full ? 1f : this.channel.y, 0, color); //val
                    return ColorHelper.withAlpha(1f, color);
                }
                var red = full ? Math.max(this.channel.x, ColorHelper.getRedFloat(this.fullColor)) : Math.min(1f - this.channel.x, ColorHelper.getRedFloat(this.fullColor));
                var green = full ? Math.max(this.channel.y, ColorHelper.getGreenFloat(this.fullColor)) : Math.min(1f - this.channel.y, ColorHelper.getGreenFloat(this.fullColor));
                var blue = full ? Math.max(this.channel.z, ColorHelper.getBlueFloat(this.fullColor)) : Math.min(1f - this.channel.z, ColorHelper.getBlueFloat(this.fullColor));
                return ColorHelper.fromFloats(1f, red, green, blue);
            }

            public void setFullColor(int v)
            {
                this.fullColor = v;
            }

            public void setValue(float value)
            {
                this.value = value;
            }

            public float getValue()
            {
                return (float) this.value;
            }

            public void setHSV(boolean hsv)
            {
                this.hsv = hsv;
            }
        }
    }
}