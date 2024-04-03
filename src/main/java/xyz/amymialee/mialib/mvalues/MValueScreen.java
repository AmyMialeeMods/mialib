package xyz.amymialee.mialib.mvalues;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.runnables.TriFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MValueScreen extends Screen {
    public static final HashMap<Class<?>, TriFunction<Integer, Integer, MValue<?>, MValueButton<?, ?>>> MVALUE_WIDGETS = new HashMap<>();
    public static final Identifier WINDOW_TEXTURE = MiaLib.id("textures/gui/mvalue/mvalue_screen.png");
    private final List<MValueButton<?, ?>> widgets = new ArrayList<>();
    private MValueCategory selectedCategory = MiaLib.MIALIB_CATEGORY;
    private int selectedCategoryPage = 0;
    private int mvaluePage = 0;

    public MValueScreen() {
        super(Text.translatable("%s.screen.mvalues".formatted(MiaLib.MOD_ID)));
    }

    @Override
    public void init() {
        var list = this.selectedCategory.values;
        this.widgets.clear();
        for (var i = 0; i < 12; i++) {
            var index = this.mvaluePage * 12 + i;
            if (index >= list.size()) break;
            var value = list.get(index);
            var left = i % 2 == 0;
            var x = this.width / 2 + (left ? -192 : 2);
            var y = this.height / 2 - 10 + (i / 2 - 2) * 24;
            this.widgets.add(this.addDrawableChild(MVALUE_WIDGETS.get(value.getClass()).apply(x, y, value)));
        }
        if (list.size() > 12) {
            this.addDrawableChild(new MValuePageWidget(this.width / 2 - 1, this.height / 2 + 87, true, Text.translatable("%s.screen.mvalues.page".formatted(MiaLib.MOD_ID), this.mvaluePage + 1, list.size() / 12 + 1),
                    list.size() <= (1 + this.mvaluePage) * 12 ? null : button -> {
                        this.mvaluePage++;
                        this.clearAndInit();
                    }));
            this.addDrawableChild(new MValuePageWidget(this.width / 2 - 11, this.height / 2 + 87, false, Text.translatable("%s.screen.mvalues.page".formatted(MiaLib.MOD_ID), this.mvaluePage + 1, list.size() / 12 + 1),
                    this.mvaluePage <= 0 ? null : button -> {
                        this.mvaluePage--;
                        this.clearAndInit();
                    }));
        }
        var categories = MValueManager.CATEGORIES;
        for (var i = 0; i < 12; i++) {
            var index = this.selectedCategoryPage * 12 + i;
            if (index >= categories.size()) break;
            var category = categories.get(index);
            var x = this.width / 2 - 194 + i * 31;
            var y = this.height / 2 - 108;
            this.addDrawableChild(new MValueCategoryWidget(x, y, category, Text.translatable(category.getTranslationKey()), category == this.selectedCategory ? null : button -> {
                this.selectedCategory = category;
                this.clearAndInit();
            }));
        }
        if (categories.size() > 12) {
            this.addDrawableChild(new MValuePageWidget(this.width / 2 + 174 + 11, this.height / 2 - 76, true, Text.translatable("%s.screen.mvalues.page".formatted(MiaLib.MOD_ID), this.selectedCategoryPage + 1, categories.size() / 12 + 1),
                    list.size() <= (1 + this.selectedCategoryPage) * 12 ? null : button -> {
                        this.selectedCategoryPage++;
                        this.clearAndInit();
                    }));
            this.addDrawableChild(new MValuePageWidget(this.width / 2 + 174 + 1, this.height / 2 - 76, false, Text.translatable("%s.screen.mvalues.page".formatted(MiaLib.MOD_ID), this.selectedCategoryPage + 1, categories.size() / 12 + 1),
                    this.selectedCategoryPage <= 0 ? null : button -> {
                        this.selectedCategoryPage--;
                        this.clearAndInit();
                    }));
        }
    }

    public void refreshWidgets() {
        this.widgets.forEach(MValueButton::refreshFromValue);
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.getMatrices().push();
        context.enableScissor(this.width / 2 - 194, this.height / 2 - 62, this.width / 2 + 194, this.height / 2 + 88);
        for (var x = 0; x <= this.width; x += 16) {
            for (var y = 0; y <= this.height; y += 16) {
                context.drawTexture(this.selectedCategory.backgroundTexture, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }
        context.disableScissor();
        RenderSystem.enableBlend();
        context.drawTexture(WINDOW_TEXTURE, this.width / 2 - 203, this.height / 2 - 80, 0, 0, 406, 177, 406, 177);
        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
        var y = this.height / 2 - 74;
        context.drawText(this.textRenderer, this.title, this.width / 2 - 192, y, 4210752, false);
        var categoryTitle = Text.translatable(this.selectedCategory.getTranslationKey());
        context.drawText(this.textRenderer, categoryTitle, this.width / 2 - this.textRenderer.getWidth(categoryTitle) / 2, y, 4210752, false);
    }

    static {
        MVALUE_WIDGETS.put(MValue.MValueBoolean.class, (x, y, value) -> new MValueBooleanButton(x, y, (MValue.MValueBoolean) value));
        MVALUE_WIDGETS.put(MValue.MValueInteger.class, (x, y, value) -> new MValueIntegerButton(x, y, (MValue.MValueInteger) value));
        MVALUE_WIDGETS.put(MValue.MValueLong.class, (x, y, value) -> new MValueLongButton(x, y, (MValue.MValueLong) value));
        MVALUE_WIDGETS.put(MValue.MValueFloat.class, (x, y, value) -> new MValueFloatButton(x, y, (MValue.MValueFloat) value));
        MVALUE_WIDGETS.put(MValue.MValueDouble.class, (x, y, value) -> new MValueDoubleButton(x, y, (MValue.MValueDouble) value));
    }

    public static class MValuePageWidget extends ButtonWidget {
        public static final Identifier BUTTON_TEX = new Identifier("fabric", "textures/gui/creative_buttons.png");
        private final boolean next;

        public MValuePageWidget(int x, int y, boolean next, Text text, PressAction consumer) {
            super(x, y, 11, 12, text, consumer, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
            this.next = next;
            this.active = consumer != null;
        }

        @Override
        public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float delta) {
            drawContext.drawTexture(BUTTON_TEX, this.getX(), this.getY(), (this.active && this.isHovered() ? 22 : 0) + (this.next ? 11 : 0), this.active ? 0 : 12, 11, 12);
            if (mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height) {
                drawContext.drawTooltip(MinecraftClient.getInstance().textRenderer, this.getMessage(), mouseX, mouseY);
            }
        }
    }

    public static class MValueCategoryWidget extends ButtonWidget {
        public static final Identifier TAB_DESELECTED = MiaLib.id("textures/gui/mvalue/tab_deselected.png");
        public static final Identifier TAB_SELECTED = MiaLib.id("textures/gui/mvalue/tab_selected.png");
        public final MValueCategory category;

        public MValueCategoryWidget(int x, int y, MValueCategory value, Text text, PressAction consumer) {
            super(x, y, 28, 32, text, consumer, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
            this.category = value;
            this.active = consumer != null;
        }

        @Override
        public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float delta) {
            drawContext.drawTexture(this.active ? TAB_DESELECTED : TAB_SELECTED, this.getX(), this.getY(), 0, 0, 28, 32, 28, 32);
            drawContext.drawItem(this.category.stackSupplier.get(), this.getX() + 5, this.getY() + 10);
            if (mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height) {
                drawContext.drawTooltip(MinecraftClient.getInstance().textRenderer, this.getMessage(), mouseX, mouseY);
            }
        }
    }

    public abstract static class MValueButton<T, K extends MValue<T>> extends ClickableWidget {
        protected final K value;

        public MValueButton(int x, int y, K value) {
            super(x, y, 190, 20, Text.translatable(value.getTranslationKey()));
            this.value = value;
            this.refreshFromValue();
        }

        public abstract void refreshFromValue();

        public void refreshMessage() {
            this.setMessage(Text.translatable(this.value.getTranslationKey()).append(Text.literal(": " + this.value.getValue())));
            this.setTooltip(Tooltip.of(Text.translatable(this.value.getDescriptionTranslationKey())));
        }

        protected int getTextureY() {
            var i = 1;
            if (!this.active) {
                i = 0;
            } else if (this.isSelected()) {
                i = 2;
            }
            return 46 + i * 20;
        }

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        public void sendChange() {
            var buf = PacketByteBufs.create();
            buf.writeIdentifier(this.value.id);
            buf.writeNbt(this.value.writeNbt(new NbtCompound()));
            ClientPlayNetworking.send(MValue.MVALUE_SYNC, buf);
        }
    }

    public static class MValueBooleanButton extends MValueButton<Boolean, MValue.MValueBoolean> {
        protected MValueBooleanButton(int x, int y, @NotNull MValue.MValueBoolean boolValue) {
            super(x, y, boolValue);
        }

        @Override
        public void refreshFromValue() {
            this.refreshMessage();
        }

        @Override
        protected void renderButton(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
            var minecraftClient = MinecraftClient.getInstance();
            context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            context.drawNineSlicedTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), 20, this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
            context.drawItem(this.value.stackSupplier.get(), this.getX() + 2, this.getY() + 2);
            context.drawNineSlicedTexture(WIDGETS_TEXTURE, this.getX() + 20, this.getY(), this.getWidth() - 20, this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawScrollableText(context, minecraftClient.textRenderer, this.getMessage(), this.getX() + 22, this.getY(), this.getX() + this.getWidth() - 2, this.getY() + this.getHeight(), (this.active ? 16777215 : 10526880) | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.value.setValueInternal(!this.value.getValue());
            this.sendChange();
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.active && this.visible) {
                if (KeyCodes.isToggle(keyCode)) {
                    this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                    this.value.setValueInternal(!this.value.getValue());
                    this.sendChange();
                    return true;
                }
            }
            return false;
        }
    }

    public abstract static class MValueSliderButton<T extends Number, K extends MValue.MValueMinMax<T>> extends MValueButton<T, K> {
        private static final Identifier TEXTURE = new Identifier("textures/gui/slider.png");
        protected double sliderValue;
        public boolean sliderFocused;

        protected MValueSliderButton(int x, int y, K value) {
            super(x, y, value);
            this.refreshFromValue();
        }

        private int getYImage() {
            var i = this.isFocused() && !this.sliderFocused ? 1 : 0;
            return i * 20;
        }

        private int getTextureV() {
            var i = !this.hovered && !this.sliderFocused ? 2 : 3;
            return i * 20;
        }

        @Override
        public void renderButton(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
            var minecraftClient = MinecraftClient.getInstance();
            context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            context.drawNineSlicedTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), 20, this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
            context.drawItem(this.value.stackSupplier.get(), this.getX() + 2, this.getY() + 2);
            context.drawNineSlicedTexture(TEXTURE, this.getX() + 20, this.getY(), this.getWidth() - 20, this.getHeight(), 20, 4, 200, 20, 0, this.getYImage());
            context.drawNineSlicedTexture(TEXTURE, this.getX() + 20 + (int)(this.sliderValue * (double)((this.width - 20) - 8)), this.getY(), 8, 20, 20, 4, 200, 20, 0, this.getTextureV());
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawScrollableText(context, minecraftClient.textRenderer, this.getMessage(), this.getX() + 22, this.getY(), this.getX() + this.getWidth() - 2, this.getY() + this.getHeight(), (this.active ? 16777215 : 10526880) | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.setValueFromMouse(mouseX);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!focused) {
                this.sliderFocused = false;
            } else {
                var guiNavigationType = MinecraftClient.getInstance().getNavigationType();
                if (guiNavigationType == GuiNavigationType.MOUSE || guiNavigationType == GuiNavigationType.KEYBOARD_TAB) {
                    this.sliderFocused = true;
                }
            }
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (KeyCodes.isToggle(keyCode)) {
                this.sliderFocused = !this.sliderFocused;
                return true;
            } else {
                if (this.sliderFocused) {
                    var bl = keyCode == GLFW.GLFW_KEY_LEFT;
                    if (bl || keyCode == GLFW.GLFW_KEY_RIGHT) {
                        var f = bl ? -1.0F : 1.0F;
                        this.setValue(this.sliderValue + (double)(f / (float)(this.width - 8)));
                        return true;
                    }
                }
                return false;
            }
        }

        private void setValueFromMouse(double mouseX) {
            this.setValue((mouseX - (double)(this.getX() + 24)) / (double)(this.width - 28));
        }

        protected abstract void setValue(double value);

        @Override
        protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
            this.setValueFromMouse(mouseX);
            super.onDrag(mouseX, mouseY, deltaX, deltaY);
        }

        @Override
        public void playDownSound(SoundManager soundManager) {}

        @Override
        public void onRelease(double mouseX, double mouseY) {
            super.playDownSound(MinecraftClient.getInstance().getSoundManager());
        }
    }

    public static class MValueIntegerButton extends MValueSliderButton<Integer, MValue.MValueInteger> {
        protected MValueIntegerButton(int x, int y, @NotNull MValue.MValueInteger value) {
            super(x, y, value);
        }

        @Override
        public void refreshFromValue() {
            this.sliderValue = (double) (this.value.getValue() - this.value.getMin()) / (this.value.getMax() - this.value.getMin());
            this.refreshMessage();
        }

        @Override
        protected void setValue(double value) {
            var clamped = MathHelper.clamp(value, 0.0, 1.0);
            if (clamped != this.sliderValue) {
                this.value.setValueInternal((int) Math.round(clamped * (this.value.getMax() - this.value.getMin()) + this.value.getMin()));
                this.sendChange();
            }
        }
    }

    public static class MValueLongButton extends MValueSliderButton<Long, MValue.MValueLong> {
        protected MValueLongButton(int x, int y, @NotNull MValue.MValueLong value) {
            super(x, y, value);
        }

        @Override
        public void refreshFromValue() {
            this.sliderValue = (double) (this.value.getValue() - this.value.getMin()) / (this.value.getMax() - this.value.getMin());
            this.refreshMessage();
        }

        @Override
        protected void setValue(double value) {
            var clamped = MathHelper.clamp(value, 0.0, 1.0);
            if (clamped != this.sliderValue) {
                this.value.setValueInternal(Math.round(clamped * (this.value.getMax() - this.value.getMin()) + this.value.getMin()));
                this.sendChange();
            }
        }
    }

    public static class MValueFloatButton extends MValueSliderButton<Float, MValue.MValueFloat> {
        protected MValueFloatButton(int x, int y, @NotNull MValue.MValueFloat value) {
            super(x, y, value);
        }

        @Override
        public void refreshFromValue() {
            this.sliderValue = (this.value.getValue() - this.value.getMin()) / (this.value.getMax() - this.value.getMin());
            this.refreshMessage();
        }

        @Override
        protected void setValue(double value) {
            var clamped = MathHelper.clamp(value, 0.0, 1.0);
            if (clamped != this.sliderValue) {
                this.value.setValueInternal((float) (clamped * (this.value.getMax() - this.value.getMin()) + this.value.getMin()));
                this.sendChange();
            }
        }
    }

    public static class MValueDoubleButton extends MValueSliderButton<Double, MValue.MValueDouble> {
        protected MValueDoubleButton(int x, int y, @NotNull MValue.MValueDouble value) {
            super(x, y, value);
        }

        @Override
        public void refreshFromValue() {
            this.sliderValue = (this.value.getValue() - this.value.getMin()) / (this.value.getMax() - this.value.getMin());
            this.refreshMessage();
        }

        @Override
        protected void setValue(double value) {
            var clamped = MathHelper.clamp(value, 0.0, 1.0);
            if (clamped != this.sliderValue) {
                this.value.setValueInternal(clamped * (this.value.getMax() - this.value.getMin()) + this.value.getMin());
                this.sendChange();
            }
        }
    }
}