package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MValueDouble extends MValueRoundable<Double> {
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
    public @NotNull CompoundTag writeNbt(@NotNull CompoundTag compound, @NotNull MValue<Double> value) {
        compound.putDouble("value", value.get());
        return compound;
    }

    @Override
    public void readNbt(@NotNull CompoundTag compound, @NotNull MValue<Double> value) {
        value.value = compound.getDoubleOr("value", this.defaultValue);
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
        if (value.type instanceof MValueMinMax<Double> minMax) {
            CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> dispatcher.register(Commands.literal("mvalue").requires(Commands.hasPermission(value.permissionCheck))
                    .then(Commands.literal(value.id.toString())
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(minMax.getMin(), minMax.getMax()))
                                    .executes(ctx -> {
                                        value.set(DoubleArgumentType.getDouble(ctx, "value"));
                                        MVServerManager.INSTANCE.onChange(value);
                                        ctx.getSource().sendSuccess(() -> Component.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                        return 1;
                                    })
                            ).executes(ctx -> {
                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                                return 1;
                            }))));
        }
    }
}
