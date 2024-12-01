package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MValueType<T> {
    protected T defaultValue;

    public void reset(@NotNull MValue<T> value) {
        value.set(this.defaultValue);
    }

    public abstract boolean set(MValue<T> mValue, T value);

    public abstract NbtCompound writeNbt(NbtCompound compound, MValue<T> value);

    public abstract void readNbt(NbtCompound compound, MValue<T> value);

    public abstract JsonElement writeJson(MValue<T> value);

    public abstract void readJson(JsonElement json, MValue<T> value);

    public abstract ClickableWidget getWidget(int x, int y, MValue<T> mValue);

    public static final class MValueBoolean extends MValueType<Boolean> {
        MValueBoolean(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public @NotNull ClickableWidget getWidget(int x, int y, MValue<Boolean> value) {
            return new MValueWidget<>(x, y, value) {
                @Override
                public void onClick(double mouseX, double mouseY) {
                    this.value.set(!this.value.get());
                }
            };
        }

        @Override
        public boolean set(@NotNull MValue<Boolean> mValue, Boolean value) {
            mValue.value = value;
            return true;
        }

        @Override
        public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Boolean> value) {
            compound.putBoolean("value", value.get());
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Boolean> value) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                value.value = compound.getBoolean("value");
            } else {
                value.set(compound.getBoolean("value"));
            }
        }

        @Override
        public @NotNull JsonElement writeJson(@NotNull MValue<Boolean> value) {
            return new JsonPrimitive(value.get());
        }

        @Override
        public void readJson(@NotNull JsonElement json, @NotNull MValue<Boolean> value) {
            value.set(json.getAsBoolean());
        }
    }

    public static abstract class MValueMinMax<T> extends MValueType<T> {
        @Override
        public @NotNull ClickableWidget getWidget(int x, int y, MValue<T> value) {
            return new MValueWidget<>(x, y, value) {
                @Override
                public void onClick(double mouseX, double mouseY) {

                }
            };
        }

        public abstract MValueMinMax<T> of(T defaultValue, T min, T max);

        public abstract T getMin();

        public abstract T getMax();
    }

    public static final class MValueInteger extends MValueMinMax<Integer> {
        private final int min;
        private final int max;

        MValueInteger(int defaultValue, int min, int max) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public @NotNull MValueMinMax<Integer> of(Integer defaultValue, Integer min, Integer max) {
            return new MValueInteger(defaultValue, min, max);
        }

        @Override
        public boolean set(@NotNull MValue<Integer> mValue, Integer value) {
            var clamped = Math.clamp(value, this.min, this.max);
            if (clamped == mValue.value) return false;
            mValue.value = value;
            return true;
        }

        @Override
        public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Integer> value) {
            compound.putInt("value", value.get());
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Integer> value) {
            value.set(compound.getInt("value"));
        }

        @Override
        public @NotNull JsonElement writeJson(@NotNull MValue<Integer> value) {
            return new JsonPrimitive(value.get());
        }

        @Override
        public void readJson(@NotNull JsonElement json, @NotNull MValue<Integer> value) {
            value.set(json.getAsInt());
        }

        @Override
        public Integer getMin() {
            return this.min;
        }

        @Override
        public Integer getMax() {
            return this.max;
        }
    }

    public static final class MValueFloat extends MValueMinMax<Float> {
        private final float min;
        private final float max;

        MValueFloat(float defaultValue, float min, float max) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public @NotNull MValueMinMax<Float> of(Float defaultValue, Float min, Float max) {
            return new MValueFloat(defaultValue, min, max);
        }

        @Override
        public boolean set(@NotNull MValue<Float> mValue, Float value) {
            var clamped = Math.clamp(value, this.min, this.max);
            if (clamped == mValue.value) return false;
            mValue.value = value;
            return true;
        }

        @Override
        public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Float> value) {
            compound.putFloat("value", value.get());
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Float> value) {
            value.set(compound.getFloat("value"));
        }

        @Override
        public @NotNull JsonElement writeJson(@NotNull MValue<Float> value) {
            return new JsonPrimitive(value.get());
        }

        @Override
        public void readJson(@NotNull JsonElement json, @NotNull MValue<Float> value) {
            value.set(json.getAsFloat());
        }

        @Override
        public Float getMin() {
            return this.min;
        }

        @Override
        public Float getMax() {
            return this.max;
        }
    }

    public static final class MValueLong extends MValueMinMax<Long> {
        private final long min;
        private final long max;

        MValueLong(long defaultValue, long min, long max) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public @NotNull MValueMinMax<Long> of(Long defaultValue, Long min, Long max) {
            return new MValueLong(defaultValue, min, max);
        }

        @Override
        public boolean set(@NotNull MValue<Long> mValue, Long value) {
            var clamped = Math.clamp(value, this.min, this.max);
            if (clamped == mValue.value) return false;
            mValue.value = value;
            return true;
        }

        @Override
        public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Long> value) {
            compound.putFloat("value", value.get());
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Long> value) {
            value.set(compound.getLong("value"));
        }

        @Override
        public @NotNull JsonElement writeJson(@NotNull MValue<Long> value) {
            return new JsonPrimitive(value.get());
        }

        @Override
        public void readJson(@NotNull JsonElement json, @NotNull MValue<Long> value) {
            value.set(json.getAsLong());
        }

        @Override
        public Long getMin() {
            return this.min;
        }

        @Override
        public Long getMax() {
            return this.max;
        }
    }

    public static final class MValueDouble extends MValueMinMax<Double> {
        private final double min;
        private final double max;

        MValueDouble(double defaultValue, double min, double max) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public @NotNull MValueMinMax<Double> of(Double defaultValue, Double min, Double max) {
            return new MValueDouble(defaultValue, min, max);
        }

        @Override
        public boolean set(@NotNull MValue<Double> mValue, Double value) {
            var clamped = Math.clamp(value, this.min, this.max);
            if (clamped == mValue.value) return false;
            mValue.value = value;
            return true;
        }

        @Override
        public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Double> value) {
            compound.putDouble("value", value.get());
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Double> value) {
            value.set(compound.getDouble("value"));
        }

        @Override
        public @NotNull JsonElement writeJson(@NotNull MValue<Double> value) {
            return new JsonPrimitive(value.get());
        }

        @Override
        public void readJson(@NotNull JsonElement json, @NotNull MValue<Double> value) {
            value.set(json.getAsDouble());
        }

        @Override
        public Double getMin() {
            return this.min;
        }

        @Override
        public Double getMax() {
            return this.max;
        }
    }

    @Environment(EnvType.CLIENT)
    public abstract static class MValueWidget<T> extends ClickableWidget {
        private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/button"), Identifier.ofVanilla("widget/button_disabled"), Identifier.ofVanilla("widget/button_highlighted"));
        public final MValue<T> value;
        public double scroll;
        public double velocity;

        public MValueWidget(int x, int y, @NotNull MValue<T> value) {
            super(x, y, 142, 18, Text.translatable(value.getTranslationKey()));
            this.value = value;
            this.setMessage(this.value.getText());
            this.setTooltip(Tooltip.of(this.value.getDescription()));
        }

        @Override
        protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
            var client = MinecraftClient.getInstance();
            var scroll = this.scroll + this.velocity * delta;
            this.hovered = context.scissorContains(mouseX, mouseY)
                    && mouseX >= this.getX()
                    && mouseY >= this.getY() - scroll
                    && mouseX < this.getX() + this.width
                    && mouseY < this.getY() + this.height - scroll;
            context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURES.get(true, this.hovered), this.getX(), this.getY(), this.width, this.height, 0xFFFFFFFF);
                context.getMatrices().push();
                context.getMatrices().translate(this.getX() + 19, this.getY() + this.getHeight() / 2f, 0);
                context.getMatrices().scale(0.75f, 0.75f, 0.5f);
                context.drawText(client.textRenderer, this.getMessage(), 0, -client.textRenderer.fontHeight / 2, 0xFFFFFFFF, true);
                context.getMatrices().pop();
            var valueText = Text.literal(String.valueOf(this.value.get()));
            context.getMatrices().push();
            context.getMatrices().translate(this.getX() + this.getWidth() - 3, this.getY() + this.getHeight() / 2f, 0);
            context.getMatrices().scale(0.75f, 0.75f, 0.5f);
            context.drawText(client.textRenderer, valueText, -client.textRenderer.getWidth(valueText), -client.textRenderer.fontHeight / 2, 0xFFFFFFFF, true);
            context.getMatrices().pop();
            context.drawItem(this.value.getStack(), this.getX() + 1, this.getY() + 1);
        }

        @Override
        public abstract void onClick(double mouseX, double mouseY);

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return super.mouseClicked(mouseX, mouseY + this.scroll, button);
        }

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }
    }
}