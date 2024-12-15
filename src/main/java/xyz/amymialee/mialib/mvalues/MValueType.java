package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.util.runnables.Consumer3;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class MValueType<T> {
    protected T defaultValue;

    public abstract boolean set(MValue<T> mValue, T value);

    public String getValueAsString(@NotNull MValue<T> value) {
        return String.valueOf(value.get());
    }

    public abstract NbtCompound writeNbt(NbtCompound compound, MValue<T> value);

    public abstract void readNbt(NbtCompound compound, MValue<T> value);

    public abstract JsonElement writeJson(MValue<T> value);

    public abstract void readJson(JsonElement json, MValue<T> value);

    public void registerCommand(@NotNull MValue<T> value) {
        if (value.clientSide) {
            this.registerClientCommand(value);
        } else {
            this.registerServerCommand(value);
        }
    }

    protected abstract void registerServerCommand(MValue<T> value);

    protected abstract void registerClientCommand(MValue<T> value);

    @Environment(EnvType.CLIENT)
    public abstract Object getWidget(int x, int y, MValue<T> mValue);

    public static class MValueBoolean extends MValueType<Boolean> {
        public MValueBoolean(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public @NotNull Object getWidget(int x, int y, MValue<Boolean> value) {
            return new MValueWidget<>(x, y, value) {
                @Override
                public void onClick(double mouseX, double mouseY) {
                    if (mouseX < this.getX() + 18) {
                        this.value.send(this.value.type.defaultValue);
                    } else {
                        this.value.send(!this.value.get());
                    }
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
            value.value = compound.getBoolean("value");
        }

        @Override
        public @NotNull JsonElement writeJson(@NotNull MValue<Boolean> value) {
            return new JsonPrimitive(value.get());
        }

        @Override
        public void readJson(@NotNull JsonElement json, @NotNull MValue<Boolean> value) {
            value.set(json.getAsBoolean());
        }

        @Override
        protected void registerServerCommand(MValue<Boolean> value) {
            CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(4))
                    .then(CommandManager.literal(value.id.toString())
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        value.set(BoolArgumentType.getBool(ctx, "enabled"));
                                        MVServerManager.INSTANCE.onChange(value);
                                        ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                        return 1;
                                    })
                            ).executes(ctx -> {
                                ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                                return 1;
                            }))));
        }

        @Override
        protected void registerClientCommand(MValue<Boolean> value) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(4))
                    .then(ClientCommandManager.literal(value.id.toString())
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        value.set(BoolArgumentType.getBool(ctx, "enabled"));
                                        ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()));
                                        return 1;
                                    })
                            ).executes(ctx -> {
                                ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()));
                                return 1;
                            }))));
        }
    }

    public static abstract class MValueMinMax<T> extends MValueType<T> {
        public abstract MValueMinMax<T> of(T defaultValue, T min, T max);

        public abstract T getMin();

        public abstract T getMax();
    }

    public static class MValueInteger extends MValueMinMax<Integer> {
        public final int min;
        public final int max;

        public MValueInteger(int defaultValue, int min, int max) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public @NotNull Object getWidget(int x, int y, MValue<Integer> value) {
            var self = this;
            return new MValueSliderWidget<>(x, y, value) {
                @Override
                public void resetSliderValue() {
                    this.sliderValue = (this.value.get() - self.getMin()) / (self.getMax() - self.getMin() + 0d);
                }

                @Override
                protected Integer getValue() {
                    return (int) (self.min + (self.getMax() - self.min) * this.sliderValue);
                }
            };
        }

        @Override
        public @NotNull MValueMinMax<Integer> of(Integer defaultValue, Integer min, Integer max) {
            return new MValueInteger(defaultValue, min, max);
        }

        @Override
        public boolean set(@NotNull MValue<Integer> mValue, Integer value) {
            mValue.value = Math.clamp(value, this.getMin(), this.getMax());
            return true;
        }

        @Override
        public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Integer> value) {
            compound.putInt("value", value.get());
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Integer> value) {
            value.value = compound.getInt("value");
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

        @Override
        protected void registerServerCommand(@NotNull MValue<Integer> value) {
            if (value.type instanceof MValueType.MValueMinMax<Integer> minMax) {
                CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.literal(value.id.toString())
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(minMax.getMin(), minMax.getMax()))
                                        .executes(ctx -> {
                                            value.set(IntegerArgumentType.getInteger(ctx, "value"));
                                            MVServerManager.INSTANCE.onChange(value);
                                            ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                            return 1;
                                        })
                                ).executes(ctx -> {
                                    ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                                    return 1;
                                }))));
            }
        }

        @Override
        protected void registerClientCommand(@NotNull MValue<Integer> value) {
            if (value.type instanceof MValueType.MValueMinMax<Integer> minMax) {
                ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(4))
                        .then(ClientCommandManager.literal(value.id.toString())
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(minMax.getMin(), minMax.getMax()))
                                        .executes(ctx -> {
                                            value.set(IntegerArgumentType.getInteger(ctx, "value"));
                                            ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()));
                                            return 1;
                                        })
                                ).executes(ctx -> {
                                    ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()));
                                    return 1;
                                }))));
            }
        }
    }

    public static class MValueLong extends MValueMinMax<Long> {
        private final long min;
        private final long max;

        public MValueLong(long defaultValue, long min, long max) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public @NotNull Object getWidget(int x, int y, MValue<Long> value) {
            var self = this;
            return new MValueSliderWidget<>(x, y, value) {
                @Override
                public void resetSliderValue() {
                    this.sliderValue = (this.value.get() - self.getMin()) / (self.getMax() - self.getMin() + 0d);
                }

                @Override
                protected Long getValue() {
                    return (long) (self.min + (self.getMax() - self.min) * this.sliderValue);
                }
            };
        }

        @Override
        public @NotNull MValueMinMax<Long> of(Long defaultValue, Long min, Long max) {
            return new MValueLong(defaultValue, min, max);
        }

        @Override
        public boolean set(@NotNull MValue<Long> mValue, Long value) {
            mValue.value = Math.clamp(value, this.getMin(), this.getMax());
            return true;
        }

        @Override
        public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Long> value) {
            compound.putFloat("value", value.get());
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Long> value) {
            value.value = compound.getLong("value");
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

        @Override
        protected void registerServerCommand(@NotNull MValue<Long> value) {
            if (value.type instanceof MValueType.MValueMinMax<Long> minMax) {
                CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.literal(value.id.toString())
                                .then(CommandManager.argument("value", LongArgumentType.longArg(minMax.getMin(), minMax.getMax()))
                                        .executes(ctx -> {
                                            value.set(LongArgumentType.getLong(ctx, "value"));
                                            MVServerManager.INSTANCE.onChange(value);
                                            ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                            return 1;
                                        })
                                ).executes(ctx -> {
                                    ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                                    return 1;
                                }))));
            }
        }

        @Override
        protected void registerClientCommand(@NotNull MValue<Long> value) {
            if (value.type instanceof MValueType.MValueMinMax<Long> minMax) {
                ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(4))
                        .then(ClientCommandManager.literal(value.id.toString())
                                .then(ClientCommandManager.argument("value", LongArgumentType.longArg(minMax.getMin(), minMax.getMax()))
                                        .executes(ctx -> {
                                            value.set(LongArgumentType.getLong(ctx, "value"));
                                            ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()));
                                            return 1;
                                        })
                                ).executes(ctx -> {
                                    ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()));
                                    return 1;
                                }))));
            }
        }
    }

    public static abstract class MValueRoundable<T> extends MValueMinMax<T> {
        private final int decimals;

        public MValueRoundable(int decimals) {
            this.decimals = decimals;
        }

        public abstract MValueRoundable<T> of(T defaultValue, T min, T max, int decimals);

        public int getDecimals() {
            return this.decimals;
        }
    }

    public static class MValueFloat extends MValueRoundable<Float> {
        private final float min;
        private final float max;

        public MValueFloat(float defaultValue, float min, float max, int decimals) {
            super(decimals);
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        public MValueFloat(float defaultValue, float min, float max) {
            this(defaultValue, min, max, 2);
        }

        @Override
        public @NotNull String getValueAsString(@NotNull MValue<Float> value) {
            var format = "%." + this.getDecimals() + "f";
            return String.format(format, value.get());
        }

        @Override
        public @NotNull Object getWidget(int x, int y, MValue<Float> value) {
            var self = this;
            return new MValueSliderWidget<>(x, y, value) {
                @Override
                public void resetSliderValue() {
                    this.sliderValue = (this.value.get() - self.getMin()) / (self.getMax() - self.getMin());
                }

                @Override
                protected Float getValue() {
                    return (float) (self.min + (self.getMax() - self.min) * this.sliderValue);
                }
            };
        }

        @Override
        public @NotNull MValueMinMax<Float> of(Float defaultValue, Float min, Float max) {
            return new MValueFloat(defaultValue, min, max);
        }

        @Override
        public MValueRoundable<Float> of(Float defaultValue, Float min, Float max, int decimals) {
            return new MValueFloat(defaultValue, min, max, decimals);
        }

        @Override
        public boolean set(@NotNull MValue<Float> mValue, Float value) {
            var big = new BigDecimal(value);
            big = big.setScale(this.getDecimals(), RoundingMode.HALF_UP);
            mValue.value = Math.clamp(big.floatValue(), this.getMin(), this.getMax());
            return true;
        }

        @Override
        public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Float> value) {
            compound.putFloat("value", value.get());
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Float> value) {
            value.value = compound.getFloat("value");
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

        @Override
        protected void registerServerCommand(@NotNull MValue<Float> value) {
            if (value.type instanceof MValueType.MValueMinMax<Float> minMax) {
                CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.literal(value.id.toString())
                                .then(CommandManager.argument("value", FloatArgumentType.floatArg(minMax.getMin(), minMax.getMax()))
                                        .executes(ctx -> {
                                            value.set(FloatArgumentType.getFloat(ctx, "value"));
                                            MVServerManager.INSTANCE.onChange(value);
                                            ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                            return 1;
                                        })
                                ).executes(ctx -> {
                                    ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                                    return 1;
                                }))));
            }
        }

        @Override
        protected void registerClientCommand(@NotNull MValue<Float> value) {
            if (value.type instanceof MValueType.MValueMinMax<Float> minMax) {
                ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(4))
                        .then(ClientCommandManager.literal(value.id.toString())
                                .then(ClientCommandManager.argument("value", FloatArgumentType.floatArg(minMax.getMin(), minMax.getMax()))
                                        .executes(ctx -> {
                                            value.set(FloatArgumentType.getFloat(ctx, "value"));
                                            ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()));
                                            return 1;
                                        })
                                ).executes(ctx -> {
                                    ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()));
                                    return 1;
                                }))));
            }
        }
    }

    public static class MValueDouble extends MValueRoundable<Double> {
        private final double min;
        private final double max;

        public MValueDouble(double defaultValue, double min, double max, int decimals) {
            super(decimals);
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        public MValueDouble(double defaultValue, double min, double max) {
            this(defaultValue, min, max, 2);
        }

        @Override
        public @NotNull String getValueAsString(@NotNull MValue<Double> value) {
            var format = "%." + this.getDecimals() + "f";
            return String.format(format, value.get());
        }

        @Override
        public @NotNull Object getWidget(int x, int y, MValue<Double> value) {
            var self = this;
            return new MValueSliderWidget<>(x, y, value) {
                @Override
                public void resetSliderValue() {
                    this.sliderValue = (this.value.get() - self.getMin()) / (self.getMax() - self.getMin());
                }

                @Override
                protected Double getValue() {
                    return self.min + (self.getMax() - self.min) * this.sliderValue;
                }
            };
        }

        @Override
        public @NotNull MValueMinMax<Double> of(Double defaultValue, Double min, Double max) {
            return new MValueDouble(defaultValue, min, max);
        }

        @Override
        public MValueRoundable<Double> of(Double defaultValue, Double min, Double max, int decimals) {
            return new MValueDouble(defaultValue, min, max, decimals);
        }

        @Override
        public boolean set(@NotNull MValue<Double> mValue, Double value) {
            var big = new BigDecimal(value);
            big = big.setScale(this.getDecimals(), RoundingMode.HALF_UP);
            mValue.value = Math.clamp(big.doubleValue(), this.getMin(), this.getMax());
            return true;
        }

        @Override
        public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Double> value) {
            compound.putDouble("value", value.get());
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Double> value) {
            value.value = compound.getDouble("value");
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

        @Override
        protected void registerServerCommand(@NotNull MValue<Double> value) {
            if (value.type instanceof MValueType.MValueMinMax<Double> minMax) {
                CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.literal(value.id.toString())
                                .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(minMax.getMin(), minMax.getMax()))
                                        .executes(ctx -> {
                                            value.set(DoubleArgumentType.getDouble(ctx, "value"));
                                            MVServerManager.INSTANCE.onChange(value);
                                            ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                            return 1;
                                        })
                                ).executes(ctx -> {
                                    ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                                    return 1;
                                }))));
            }
        }

        @Override
        protected void registerClientCommand(@NotNull MValue<Double> value) {
            if (value.type instanceof MValueType.MValueMinMax<Double> minMax) {
                ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(4))
                        .then(ClientCommandManager.literal(value.id.toString())
                                .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(minMax.getMin(), minMax.getMax()))
                                        .executes(ctx -> {
                                            value.set(DoubleArgumentType.getDouble(ctx, "value"));
                                            ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()));
                                            return 1;
                                        })
                                ).executes(ctx -> {
                                    ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()));
                                    return 1;
                                }))));
            }
        }
    }

    public static class MValuePercent extends MValueDouble {
        public MValuePercent(double defaultValue, double min, double max) {
            super(defaultValue, min, max, 0);
        }

        @Override
        public @NotNull String getValueAsString(@NotNull MValue<Double> value) {
            return "%.0f%%".formatted(value.get() * 100);
        }
    }

    @Environment(EnvType.CLIENT)
    public abstract static class MValueWidget<T> extends ClickableWidget {
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
            context.drawGuiTexture(RenderLayer::getGuiTextured, textures.get(true, this.hovered), this.getX(), this.getY(), 18, this.height, 0xFFFFFFFF);
            context.drawGuiTexture(RenderLayer::getGuiTextured, textures.get(true, this.hovered), this.getX() + 18, this.getY(), this.width - 18, this.height, 0xFFFFFFFF);
            context.drawItem(this.value.getStack(), this.getX() + 1, this.getY() + 1);
            final Consumer3<Float, Float, Float> moveAndScale = (x, y, s) -> {
                context.getMatrices().push();
                context.getMatrices().translate(x, y, 0);
                context.getMatrices().scale(s, s, 1f);
            };
            var scale = 0.7f;
            moveAndScale.accept(this.getX() + this.getWidth() - 3f, this.getY() + this.getHeight() / 2f, scale);
            var valueText = Text.literal(this.value.type.getValueAsString(this.value));
            context.drawText(client.textRenderer, valueText, -client.textRenderer.getWidth(valueText), -client.textRenderer.fontHeight / 2, 0xFFFFFFFF, true);
            context.getMatrices().pop();
            moveAndScale.accept(this.getX() + 21f, this.getY() + this.getHeight() / 2f, scale);
            var wrapLines = client.textRenderer.wrapLines(this.getMessage(), 165 - client.textRenderer.getWidth(valueText));
            for (var i = 0; i < wrapLines.size(); i++) {
                var text = wrapLines.get(i);
                var y = -(wrapLines.size()) * client.textRenderer.fontHeight * .5 + client.textRenderer.fontHeight * i;
                context.drawText(client.textRenderer, text, 0, (int) y, 0xFFFFFFFF, true);
            }
            context.getMatrices().pop();
        }

        @Override
        public abstract void onClick(double mouseX, double mouseY);

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

    @Environment(EnvType.CLIENT)
    public abstract static class MValueSliderWidget<T> extends MValueWidget<T> {
        public double sliderValue;
        public boolean sliderFocused;
        public double mouseX;

        public MValueSliderWidget(int x, int y, @NotNull MValue<T> value) {
            super(x, y, value);
            this.resetSliderValue();
        }

        public abstract void resetSliderValue();

        private Identifier getTexture() {
            if (this.value.clientSide) return this.isFocused() && !this.sliderFocused ? CLIENT_SLIDER_HIGHLIGHTED_TEXTURE : CLIENT_SLIDER_TEXTURE;
            return this.isFocused() && !this.sliderFocused ? SLIDER_HIGHLIGHTED_TEXTURE : SLIDER_TEXTURE;
        }

        private Identifier getHandleTexture() {
            if (this.value.clientSide) return !this.hovered && !this.sliderFocused ? CLIENT_SLIDER_HANDLE_TEXTURE : CLIENT_SLIDER_HANDLE_HIGHLIGHTED_TEXTURE;
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
            context.drawGuiTexture(RenderLayer::getGuiTextured, textures.get(true, this.hovered), this.getX(), this.getY(), 18, this.height, 0xFFFFFFFF);
            context.drawGuiTexture(RenderLayer::getGuiTextured, this.getTexture(), this.getX() + 18, this.getY(), this.getWidth() - 18, this.getHeight(), ColorHelper.getWhite(this.alpha));
            context.drawGuiTexture(RenderLayer::getGuiTextured, this.getHandleTexture(), this.getX() + 18 + (int)(this.sliderValue * (double)(this.width - 8 - 18)), this.getY(), 8, this.getHeight(), ColorHelper.getWhite(this.alpha));
            context.drawItem(this.value.getStack(), this.getX() + 1, this.getY() + 1);
            final Consumer3<Float, Float, Float> moveAndScale = (x, y, s) -> {
                context.getMatrices().push();
                context.getMatrices().translate(x, y, 0);
                context.getMatrices().scale(s, s, 1f);
            };
            var scale = 0.7f;
            moveAndScale.accept(this.getX() + this.getWidth() - 3f, this.getY() + this.getHeight() / 2f, scale);
            var valueText = Text.literal(this.value.type.getValueAsString(this.value));
            context.drawText(client.textRenderer, valueText, -client.textRenderer.getWidth(valueText), -client.textRenderer.fontHeight / 2, 0xFFFFFFFF, true);
            context.getMatrices().pop();
            moveAndScale.accept(this.getX() + 21f, this.getY() + this.getHeight() / 2f, scale);
            var wrapLines = client.textRenderer.wrapLines(this.getMessage(), 165 - client.textRenderer.getWidth(valueText));
            for (var i = 0; i < wrapLines.size(); i++) {
                var text = wrapLines.get(i);
                var y = -(wrapLines.size()) * client.textRenderer.fontHeight * .5 + client.textRenderer.fontHeight * i;
                context.drawText(client.textRenderer, text, 0, (int) y, 0xFFFFFFFF, true);
            }
            context.getMatrices().pop();
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (mouseX < this.getX() + 18) {
                this.value.send(this.value.type.defaultValue);
                this.resetSliderValue();
            } else {
                this.setValueFromMouse(mouseX);
            }
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
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
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (KeyCodes.isToggle(keyCode)) {
                this.sliderFocused = !this.sliderFocused;
                return true;
            }
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

        private void setValueFromMouse(double mouseX) {
            this.setValue((mouseX - (double)(this.getX() + 18 + 4)) / (double)(this.width - 18 - 8));
        }

        private void setValue(double value) {
            var old = this.sliderValue;
            this.sliderValue = MathHelper.clamp(value, 0.0, 1.0);
            if (old != this.sliderValue) {
                this.value.value = this.getValue();
            }
        }

        @Override
        protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
            if (!this.sliderFocused) return;
            this.setValueFromMouse(mouseX);
            super.onDrag(mouseX, mouseY, deltaX, deltaY);
        }

        @Override
        public void playDownSound(SoundManager soundManager) {}

        protected abstract T getValue();
    }
}