package xyz.amymialee.mialib.values;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class MIntegerValue extends MValue<Integer> {
    private final int min;
    private final int max;

    public MIntegerValue(Identifier id, Function<Integer, ItemStack> displayStack, int defaultValue, int min, int max) {
        super(id, displayStack, MValueType.INTEGER, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer getScaledValue(double value) {
        return MathHelper.lerp((float) MathHelper.clamp(value, 0.0d, 1.0d), this.min, this.max);
    }

    @Override
    public double getScaledValue() {
        return (double) (super.getValue() - this.min) / (this.max - this.min);
    }
}