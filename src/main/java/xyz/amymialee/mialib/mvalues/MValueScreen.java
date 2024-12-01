package xyz.amymialee.mialib.mvalues;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import xyz.amymialee.mialib.*;

@Environment(EnvType.CLIENT)
public class MValueScreen extends Screen {
    public static final Identifier WINDOW_TEXTURE = Mialib.id("textures/gui/mvalue/mvalue_screen.png");
    public static final Identifier DARK_OAK_PLANKS = Identifier.ofVanilla("textures/block/dark_oak_planks.png");
    public static final int WIDTH = 370;
    public static final int HEIGHT = 200;
    private MValueCategory selectedCategory = MValue.DEFAULT_CATEGORY;
    private double categoryScroll;
    private double categoryVelocity;
    private double valueScroll;
    private double valueVelocity;

    public MValueScreen() {
        super(Text.empty());
    }

    @Override
    public void init() {
        var centreX = this.width / 2;
        var centreY = this.height / 2;
        var categories = MValueManager.CATEGORIES;
        for (var i = 0; i < categories.size(); i++) {
            var category = categories.get(i);
            var x = centreX + 7 + 3 - WIDTH / 2 + (i % 2 == 0 ? 0 : 27);
            var y = centreY + 7 + 3 - HEIGHT / 2 + (i / 2) * 27;
            this.addDrawableChild(category.getWidget(x, y, category == this.selectedCategory ? null : button -> {
                this.selectedCategory = category;
                this.valueScroll = 0;
                this.valueVelocity = 0;
                this.clearAndInit();
            }));
        }
        if (this.client == null) return;
        var values = this.selectedCategory.getValues(this.client.player);
        for (var i = 0; i < values.size(); i++) {
            var value = values.get(i);
            var x = centreX + 70 + 3 - WIDTH / 2 + (i % 2 == 0 ? 0 : 145);
            var y = centreY + 7 + 3 - HEIGHT / 2 + (i / 2) * 21;
            this.addDrawableChild(value.getWidget(x, y));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        var left = this.width / 2 - WIDTH / 2 + 67;
        if (mouseX < left) {
            this.categoryVelocity -= verticalAmount;
        } else {
            this.valueVelocity -= verticalAmount;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client == null) return;
        this.renderBackground(context, mouseX, mouseY, delta);
        this.categoryScroll = Math.clamp(this.categoryScroll + this.categoryVelocity, 0, Math.max(0, (MValueManager.CATEGORIES.size() / 2f + 1) * 27 - 7 * 27));
        this.categoryVelocity *= .96f;
        this.valueScroll = Math.clamp(this.valueScroll + this.valueVelocity, 0, Math.max(0, (this.selectedCategory.getValues(this.client.player).size() / 2f + 1) * 21 - 9 * 21));
        this.valueVelocity *= .96f;
        var right = this.width / 2 + WIDTH / 2;
        var left = this.width / 2 - WIDTH / 2;
        var backWidth = this.selectedCategory.width;
        var backHeight = this.selectedCategory.height;
        {
            var scrollAmount = this.categoryScroll + this.categoryVelocity * delta;
            context.enableScissor(left + 7, this.height / 2 - 93, left + 64, this.height / 2 + 93);
            for (var x = left + 7; x <= left + 64; x += backWidth) {
                for (var y = this.height / 2f - 93 - scrollAmount - backHeight; y <= this.height / 2f + 93; y += backHeight) {
                    context.mialib$drawTexture(DARK_OAK_PLANKS, x, (float) y, backWidth, backHeight, backWidth, backHeight);
                }
            }
            context.disableScissor();
        }
        {
            var scrollAmount = this.valueScroll + this.valueVelocity * delta;
            context.enableScissor(left + 70, this.height / 2 - 93, right - 7, this.height / 2 + 93);
            for (var x = left + 70; x <= right - 7; x += backWidth) {
                for (var y = this.height / 2f - 93 - scrollAmount - backHeight; y <= this.height / 2f + 93; y += backHeight) {
                    context.mialib$drawTexture(this.selectedCategory.backgroundTexture, x, (float) y, backWidth, backHeight, backWidth, backHeight);
                }
            }
            context.disableScissor();
        }
        context.mialib$drawTexture(WINDOW_TEXTURE, left, this.height / 2f - 100, 0, 0, 370, 200, 370, 200);
        context.enableScissor(left + 7, this.height / 2 - 93, right - 7, this.height / 2 + 93);
        for (var drawable : this.drawables) {
            context.getMatrices().push();
            if (drawable instanceof MValueCategory.MValueCategoryWidget widget) {
                widget.scroll = this.categoryScroll;
                widget.velocity = this.categoryVelocity;
                context.getMatrices().translate(0, -(this.categoryScroll + this.categoryVelocity * delta), 0);
            }
            if (drawable instanceof MValueType.MValueWidget<?> widget) {
                widget.scroll = this.valueScroll;
                widget.velocity = this.valueVelocity;
                context.getMatrices().translate(0, -(this.valueScroll + this.valueVelocity * delta), 0);
            }
            drawable.render(context, mouseX, mouseY, delta);
            context.getMatrices().pop();
        }
        context.disableScissor();
        var categoryTitle = Text.translatable(this.selectedCategory.getTranslationKey());
        context.drawText(this.textRenderer, categoryTitle, this.width / 2 - this.textRenderer.getWidth(categoryTitle) / 2, this.height / 2 - 110, 0xFFFFFFFF, true);
    }

//    public static class MValueBooleanButton extends MValueButton<Boolean, MValue<Boolean>> {
//        protected MValueBooleanButton(int x, int y, @NotNull MValue<Boolean> boolValue) {
//            super(x, y, boolValue);
//        }
//
//        @Override
//        public void refreshFromValue() {
//            this.refreshMessage();
//        }
//
//        @Override
//        protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
//            var minecraftClient = MinecraftClient.getInstance();
//            RenderSystem.enableBlend();
//            RenderSystem.enableDepthTest();
//            context.mialib$drawTexture(BUTTON_TEXTURES.get(this.active, this.isSelected()), this.getX(), this.getY(), 20, this.getHeight(), 200, 20);
//            context.drawItem(this.value.getStack(), this.getX() + 2, this.getY() + 2);
//            context.mialib$drawTexture(BUTTON_TEXTURES.get(this.active, this.isSelected()), this.getX() + 20, this.getY(), this.getWidth() - 20, this.getHeight(), 200, 20);
//            drawScrollableText(context, minecraftClient.textRenderer, this.getMessage(), this.getX() + 22, this.getY(), this.getX() + this.getWidth() - 2, this.getY() + this.getHeight(), (this.active ? 16777215 : 10526880) | MathHelper.ceil(this.alpha * 255.0F) << 24);
//        }
//
//        @Override
//        public void onClick(double mouseX, double mouseY) {
//            if (mouseX - (double)(this.getX() + 20) < 0) {
//                this.value.sendValue(this.value.getDefaultValue());
//                return;
//            }
//            this.value.sendValue(!this.value.getValue());
//        }
//
//        @Override
//        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//            if (this.active && this.visible) {
//                if (KeyCodes.isToggle(keyCode)) {
//                    this.playDownSound(MinecraftClient.getInstance().getSoundManager());
//                    this.value.sendValue(!this.value.getValue());
//                    return true;
//                }
//            }
//            return false;
//        }
//    }
//
//    public abstract static class MValueSliderButton<T extends Number, K extends MValue.MValueMinMax<T>> extends MValueButton<T, K> {
//        public static final Identifier SLIDER_TEXTURE = Identifier.ofVanilla("widget/slider");
//        public static final Identifier SLIDER_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/slider_highlighted");
//        public static final Identifier SLIDER_HANDLE_TEXTURE = Identifier.ofVanilla("widget/slider_handle");
//        public static final Identifier SLIDER_HANDLE_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/slider_handle_highlighted");
//        protected double sliderValue;
//        public boolean sliderFocused;
//
//        protected MValueSliderButton(int x, int y, K value) {
//            super(x, y, value);
//            this.refreshFromValue();
//        }
//
//        protected final Identifier getSliderTexture(boolean highlighted) {
//            return highlighted ? SLIDER_HIGHLIGHTED_TEXTURE : SLIDER_TEXTURE;
//        }
//
//        protected final Identifier getSliderHandleTexture(boolean highlighted) {
//            return highlighted ? SLIDER_HANDLE_HIGHLIGHTED_TEXTURE : SLIDER_HANDLE_TEXTURE;
//        }
//
//        @Override
//        protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
//            var minecraftClient = MinecraftClient.getInstance();
//            RenderSystem.enableBlend();
//            RenderSystem.defaultBlendFunc();
//            RenderSystem.enableDepthTest();
//            context.mialib$drawTexture(BUTTON_TEXTURES.get(this.active, this.hovered), this.getX(), this.getY(), 20, this.getHeight(), 200, 20);
//            context.drawItem(this.value.getStack(), this.getX() + 2, this.getY() + 2);
//            context.mialib$drawTexture(this.getSliderTexture(this.isFocused() && !this.sliderFocused && this.hovered), this.getX() + 20, this.getY(), this.getWidth() - 20, this.getHeight(), 8, 20);
//            context.mialib$drawTexture(this.getSliderHandleTexture(this.hovered || this.sliderFocused), this.getX() + 20 + (int)(this.sliderValue * (double)((this.width - 20) - 8)), this.getY(), 8, 20, 8, 20);
//            drawScrollableText(context, minecraftClient.textRenderer, this.getMessage(), this.getX() + 22, this.getY(), this.getX() + this.getWidth() - 2, this.getY() + this.getHeight(), (this.active ? 16777215 : 10526880) | MathHelper.ceil(this.alpha * 255.0F) << 24);
//        }
//
//        @Override
//        public void onClick(double mouseX, double mouseY) {
//            if (mouseX - (double)(this.getX() + 20) < 0) {
//                this.value.sendValue(this.value.getDefaultValue());
//                return;
//            }
//            this.setValueFromMouse(mouseX);
//        }
//
//        @Override
//        public void setFocused(boolean focused) {
//            super.setFocused(focused);
//            if (!focused) {
//                this.sliderFocused = false;
//            } else {
//                var guiNavigationType = MinecraftClient.getInstance().getNavigationType();
//                if (guiNavigationType == GuiNavigationType.MOUSE || guiNavigationType == GuiNavigationType.KEYBOARD_TAB) {
//                    this.sliderFocused = true;
//                }
//            }
//        }
//
//        @Override
//        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//            if (KeyCodes.isToggle(keyCode)) {
//                this.sliderFocused = !this.sliderFocused;
//                return true;
//            } else {
//                if (this.sliderFocused) {
//                    var bl = keyCode == GLFW.GLFW_KEY_LEFT;
//                    if (bl || keyCode == GLFW.GLFW_KEY_RIGHT) {
//                        var f = bl ? -1.0F : 1.0F;
//                        this.setValue(this.sliderValue + (double)(f / (float)(this.width - 8)));
//                        return true;
//                    }
//                }
//                return false;
//            }
//        }
//
//        private void setValueFromMouse(double mouseX) {
//            if (mouseX - (double)(this.getX() + 24) < 0) return;
//            this.setValue((mouseX - (double)(this.getX() + 24)) / (double)(this.width - 28));
//        }
//
//        protected abstract void setValue(double value);
//
//        @Override
//        protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
//            this.setValueFromMouse(mouseX);
//            super.onDrag(mouseX, mouseY, deltaX, deltaY);
//        }
//
//        @Override
//        public void playDownSound(SoundManager soundManager) {}
//
//        @Override
//        public void onRelease(double mouseX, double mouseY) {
//            super.playDownSound(MinecraftClient.getInstance().getSoundManager());
//        }
//    }
//
//    public static class MValueIntegerButton extends MValueSliderButton<Integer, MValue.MValueInteger> {
//        protected MValueIntegerButton(int x, int y, @NotNull MValue.MValueInteger value) {
//            super(x, y, value);
//        }
//
//        @Override
//        public void refreshFromValue() {
//            this.sliderValue = (double) (this.value.getValue() - this.value.getMin()) / (this.value.getMax() - this.value.getMin());
//            this.refreshMessage();
//        }
//
//        @Override
//        protected void setValue(double value) {
//            var clamped = MathHelper.clamp(value, 0.0, 1.0);
//            if (clamped != this.sliderValue) {
//                this.value.sendValue((int) Math.round(clamped * (this.value.getMax() - this.value.getMin()) + this.value.getMin()));
//            }
//        }
//    }
//
//    public static class MValueLongButton extends MValueSliderButton<Long, MValue.MValueLong> {
//        protected MValueLongButton(int x, int y, @NotNull MValue.MValueLong value) {
//            super(x, y, value);
//        }
//
//        @Override
//        public void refreshFromValue() {
//            this.sliderValue = (double) (this.value.getValue() - this.value.getMin()) / (this.value.getMax() - this.value.getMin());
//            this.refreshMessage();
//        }
//
//        @Override
//        protected void setValue(double value) {
//            var clamped = MathHelper.clamp(value, 0.0, 1.0);
//            if (clamped != this.sliderValue) {
//                this.value.sendValue(Math.round(clamped * (this.value.getMax() - this.value.getMin()) + this.value.getMin()));
//            }
//        }
//    }
//
//    public static class MValueFloatButton extends MValueSliderButton<Float, MValue.MValueFloat> {
//        protected MValueFloatButton(int x, int y, @NotNull MValue.MValueFloat value) {
//            super(x, y, value);
//        }
//
//        @Override
//        public void refreshFromValue() {
//            this.sliderValue = (this.value.getValue() - this.value.getMin()) / (this.value.getMax() - this.value.getMin());
//            this.refreshMessage();
//        }
//
//        @Override
//        public void refreshMessage() {
//            this.setMessage(Text.translatable(this.value.getTranslationKey()).append(Text.literal(": %.2f".formatted(this.value.getValue()))));
//            this.setTooltip(Tooltip.of(Text.translatable(this.value.getDescriptionTranslationKey())));
//        }
//
//        @Override
//        protected void setValue(double value) {
//            var clamped = MathHelper.clamp(value, 0.0, 1.0);
//            if (clamped != this.sliderValue) {
//                this.value.sendValue((float) (clamped * (this.value.getMax() - this.value.getMin()) + this.value.getMin()));
//            }
//        }
//    }
//
//    public static class MValueDoubleButton extends MValueSliderButton<Double, MValue.MValueDouble> {
//        protected MValueDoubleButton(int x, int y, @NotNull MValue.MValueDouble value) {
//            super(x, y, value);
//        }
//
//        @Override
//        public void refreshFromValue() {
//            this.sliderValue = (this.value.getValue() - this.value.getMin()) / (this.value.getMax() - this.value.getMin());
//            this.refreshMessage();
//        }
//
//        @Override
//        public void refreshMessage() {
//            this.setMessage(Text.translatable(this.value.getTranslationKey()).append(Text.literal(": %.2f".formatted(this.value.getValue()))));
//            this.setTooltip(Tooltip.of(Text.translatable(this.value.getDescriptionTranslationKey())));
//        }
//
//        @Override
//        protected void setValue(double value) {
//            var clamped = MathHelper.clamp(value, 0.0, 1.0);
//            if (clamped != this.sliderValue) {
//                this.value.sendValue(clamped * (this.value.getMax() - this.value.getMin()) + this.value.getMin());
//            }
//        }
//    }
}