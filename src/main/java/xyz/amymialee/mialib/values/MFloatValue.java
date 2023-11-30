package xyz.amymialee.mialib.values;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class MFloatValue extends MValue<Float> {
    private final float min;
    private final float max;

    public MFloatValue(Identifier id, Function<Float, ItemStack> displayStack, float defaultValue, float min, float max) {
        super(id, displayStack, MValueType.FLOAT, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public Float getScaledValue(double value) {
        return MathHelper.lerp((float) MathHelper.clamp(value, 0.0d, 1.0d), this.min, this.max);
    }

    @Override
    public double getScaledValue() {
        return (super.getValue() - this.min) / (this.max - this.min);
    }
}