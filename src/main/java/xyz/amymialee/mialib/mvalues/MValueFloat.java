package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MValueFloat extends MValueRoundable<Float> {
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
        return new xyz.amymialee.mialib.mvalues.MValueFloat(defaultValue, min, max);
    }

    @Override
    public MValueRoundable<Float> of(Float defaultValue, Float min, Float max, int decimals) {
        return new xyz.amymialee.mialib.mvalues.MValueFloat(defaultValue, min, max, decimals);
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
        value.value = compound.getFloat("value", this.defaultValue);
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
        if (value.type instanceof MValueMinMax<Float> minMax) {
            CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(CommandManager.requirePermissionLevel(value.permissionCheck))
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
        if (value.type instanceof MValueMinMax<Float> minMax) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(CommandManager.requirePermissionLevel(value.permissionCheck))
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
