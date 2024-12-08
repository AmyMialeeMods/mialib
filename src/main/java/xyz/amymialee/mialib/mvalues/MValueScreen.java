package xyz.amymialee.mialib.mvalues;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

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
        if (this.client == null || this.client.player == null) return;
        var centreX = this.width / 2;
        var centreY = this.height / 2;
        var categories = MValueCategory.CATEGORIES;
        for (var i = 0; i < categories.size(); i++) {
            var category = categories.get(i);
            if (category.getValues(this.client.player).isEmpty()) continue;
            var x = centreX + 7 + 3 - WIDTH / 2 + (i % 2 == 0 ? 0 : 25);
            var y = centreY + 7 + 3 - HEIGHT / 2 + (i / 2) * 25;
            this.addDrawableChild(category.getWidget(x, y, category == this.selectedCategory ? null : button -> {
                this.selectedCategory = category;
                this.valueScroll = 0;
                this.valueVelocity = 0;
                this.clearAndInit();
            }));
        }
        var values = this.selectedCategory.getValues(this.client.player);
        for (var i = 0; i < values.size(); i++) {
            var value = values.get(i);
            var x = centreX + 66 + 3 - WIDTH / 2 + (i % 2 == 0 ? 0 : 147);
            var y = centreY + 7 + 3 - HEIGHT / 2 + (i / 2) * 21;
            this.addDrawableChild(((Element & Drawable & Selectable) value.getWidget(x, y)));
        }
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, this.height / 2 + HEIGHT / 2 + 60, 200, 80).build());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        var left = this.width / 2 - WIDTH / 2 + 63;
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
        this.categoryScroll = Math.clamp(this.categoryScroll + this.categoryVelocity, 0, Math.max(0, (MValueCategory.CATEGORIES.size() / 2f + 1) * 25 - 7.75 * 25));
        this.categoryVelocity *= .96f;
        this.valueScroll = Math.clamp(this.valueScroll + this.valueVelocity, 0, Math.max(0, (this.selectedCategory.getValues(this.client.player).size() / 2f + 1) * 21 - 9 * 21));
        this.valueVelocity *= .96f;
        var right = this.width / 2 + WIDTH / 2;
        var left = this.width / 2 - WIDTH / 2;
        var backWidth = this.selectedCategory.width;
        var backHeight = this.selectedCategory.height;
        {
            var scrollAmount = this.categoryScroll + this.categoryVelocity * delta;
            context.enableScissor(left + 7, this.height / 2 - 93, left + 60, this.height / 2 + 93);
            for (var x = left + 7; x <= left + 60; x += backWidth) {
                for (var y = this.height / 2f - 93 - scrollAmount - backHeight; y <= this.height / 2f + 93; y += backHeight) {
                    context.mialib$drawTexture(DARK_OAK_PLANKS, x, (float) y, backWidth, backHeight, backWidth, backHeight);
                }
            }
            context.disableScissor();
        }
        {
            var scrollAmount = this.valueScroll + this.valueVelocity * delta;
            context.enableScissor(left + 66, this.height / 2 - 93, right - 7, this.height / 2 + 93);
            for (var x = left + 66; x <= right - 7; x += backWidth) {
                for (var y = this.height / 2f - 93 - scrollAmount - backHeight; y <= this.height / 2f + 93; y += backHeight) {
                    context.mialib$drawTexture(this.selectedCategory.backgroundTexture, x, (float) y, backWidth, backHeight, backWidth, backHeight);
                }
            }
            context.disableScissor();
        }
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
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 180);
        context.mialib$drawTexture(WINDOW_TEXTURE, left, this.height / 2f - 100, 370, 200, 370, 200);
        var categoryTitle = Text.translatable(this.selectedCategory.getTranslationKey());
        context.drawText(this.textRenderer, categoryTitle, (int) (this.width / 2f - this.textRenderer.getWidth(categoryTitle) / 2f - WIDTH / 2f + 214.5f), this.height / 2 - 110, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, Text.translatable("mialib.screen.mvalues"), this.width / 2 - WIDTH / 2, this.height / 2 - 110, 0xFFFFFFFF, true);
        context.getMatrices().pop();
    }

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