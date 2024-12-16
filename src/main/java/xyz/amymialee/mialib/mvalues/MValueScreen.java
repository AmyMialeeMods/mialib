package xyz.amymialee.mialib.mvalues;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xyz.amymialee.mialib.Mialib;

import java.util.function.Consumer;

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
        if (categories.size() > 14) {
            this.addDrawableChild(new ScrollButtonWidget(centreX - WIDTH / 2 + 23 + 11, centreY + HEIGHT / 2 - 5, ScrollButtonWidget.Type.NEXT, (a) -> this.categoryVelocity += a));
            this.addDrawableChild(new ScrollButtonWidget(centreX - WIDTH / 2 + 23, centreY + HEIGHT / 2 - 5, ScrollButtonWidget.Type.PREVIOUS, (a) -> this.categoryVelocity -= a));
        }
        for (var i = 0; i < categories.size(); i++) {
            var category = categories.get(i);
            if (MValue.INVISIBLE_CATEGORY == category || category.getValues(this.client.player).isEmpty()) continue;
            if (this.selectedCategory.getValues(this.client.player).isEmpty()) this.selectedCategory = category;
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
        if (values.size() > 16) {
            this.addDrawableChild(new ScrollButtonWidget((int) (centreX - (11f / 2f) - WIDTH / 2f + 209.5f + 11), centreY + HEIGHT / 2 - 5, ScrollButtonWidget.Type.NEXT, (a) -> this.valueVelocity += a));
            this.addDrawableChild(new ScrollButtonWidget((int) (centreX - (11f / 2f) - WIDTH / 2f + 209.5f), centreY + HEIGHT / 2 - 5, ScrollButtonWidget.Type.PREVIOUS, (a) -> this.valueVelocity -= a));
        }
        for (var i = 0; i < values.size(); i++) {
            var value = values.get(i);
            var x = centreX + 66 + 3 - WIDTH / 2 + (i % 2 == 0 ? 0 : 147);
            var y = centreY + 7 + 3 - HEIGHT / 2 + (i / 2) * 21;
            this.addDrawableChild(((Element & Drawable & Selectable) value.getWidget(x, y)));
        }
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, centreY + HEIGHT / 2 + 10, 200, 20).build());
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
        for (var drawable : this.drawables) {
            context.getMatrices().push();
            if (drawable instanceof MValueCategory.MValueCategoryWidget widget) {
                context.enableScissor(left + 7, this.height / 2 - 93, right - 7, this.height / 2 + 93);
                widget.scroll = this.categoryScroll;
                widget.velocity = this.categoryVelocity;
                context.getMatrices().translate(0, -(this.categoryScroll + this.categoryVelocity * delta), 0);
            } else if (drawable instanceof MValueType.MValueWidget<?> widget) {
                context.enableScissor(left + 7, this.height / 2 - 93, right - 7, this.height / 2 + 93);
                widget.scroll = this.valueScroll;
                widget.velocity = this.valueVelocity;
                context.getMatrices().translate(0, -(this.valueScroll + this.valueVelocity * delta), 0);
            } else {
                context.enableScissor(0, 0, this.width, this.height);
                context.getMatrices().translate(0, 0, 190);
            }
            drawable.render(context, mouseX, mouseY, delta);
            context.disableScissor();
            context.getMatrices().pop();
        }
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 180);
        context.mialib$drawTexture(WINDOW_TEXTURE, left, this.height / 2f - 100, 370, 200, 370, 200);
        var categoryTitle = Text.translatable(this.selectedCategory.getTranslationKey());
        context.drawText(this.textRenderer, categoryTitle, (int) (this.width / 2f - this.textRenderer.getWidth(categoryTitle) / 2f - WIDTH / 2f + 214.5f), this.height / 2 - 110, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, Text.translatable("mialib.screen.mvalues"), this.width / 2 - WIDTH / 2, this.height / 2 - 110, 0xFFFFFFFF, true);
        context.getMatrices().pop();
    }

    public static class ScrollButtonWidget extends ButtonWidget {
        public static final Identifier SCROLL_BUTTONS = Identifier.of("fabric", "textures/gui/creative_buttons.png");
        private final Consumer<Float> action;
        private final Type type;

        public ScrollButtonWidget(int x, int y, @NotNull Type type, Consumer<Float> action) {
            super(x, y, 11, 12, type.text, (a) -> {
            }, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
            this.action = action;
            this.type = type;
        }

        @Override
        protected void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
            if (!this.visible) return;
            if (this.isFocused()) {
                if (this.isHovered()) {
                    this.action.accept(delta);
                } else {
                    this.setFocused(false);
                }
            }
            var u = this.active && this.isHovered() ? 22 : 0;
            var v = this.active ? 0 : 12;
            drawContext.drawTexture(RenderLayer::getGuiTextured, SCROLL_BUTTONS, this.getX(), this.getY(), u + (this.type == Type.NEXT ? 11 : 0), v, 11, 12, 256, 256);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                this.setFocused(false);
                return true;
            }
            return super.mouseReleased(mouseX, mouseY, button);
        }

        public enum Type {
            NEXT(Text.literal(">")),
            PREVIOUS(Text.literal("<"));

            final Text text;

            Type(Text text) {
                this.text = text;
            }
        }
    }
}