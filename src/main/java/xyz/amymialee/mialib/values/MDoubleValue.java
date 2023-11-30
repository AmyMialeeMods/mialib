package xyz.amymialee.mialib.values;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class MDoubleValue extends MValue<Double> {
    private final double min;
    private final double max;

    public MDoubleValue(Identifier id, Function<Double, ItemStack> displayStack, double defaultValue, double min, double max) {
        super(id, displayStack, MValueType.DOUBLE, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public Double getScaledValue(double value) {
        return MathHelper.lerp(MathHelper.clamp(value, 0.0d, 1.0d), this.min, this.max);
    }

    @Override
    public double getScaledValue() {
        return (super.getValue() - this.min) / (this.max - this.min);
    }
}