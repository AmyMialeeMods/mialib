package xyz.amymialee.mialib.values;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class MLongValue extends MValue<Long> {
    private final long min;
    private final long max;

    public MLongValue(Identifier id, Function<Long, ItemStack> displayStack, long defaultValue, long min, long max) {
        super(id, displayStack, MValueType.LONG, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public Long getScaledValue(double value) {
        return (long) (this.min + MathHelper.clamp(value, 0.0d, 1.0d) * (this.max - this.min));
    }

    @Override
    public double getScaledValue() {
        return (double) (super.getValue() - this.min) / (this.max - this.min);
    }
}