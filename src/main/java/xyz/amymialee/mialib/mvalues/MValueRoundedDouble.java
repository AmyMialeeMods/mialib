package xyz.amymialee.mialib.mvalues;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.util.runnables.HoldingFunction;

import java.util.function.Function;

public class MValueRoundedDouble extends MValue.MValueDouble {
    public MValueRoundedDouble(MValueCategory category, Identifier id, ItemStack stack, double defaultValue, double min, double max) {
        super(category, id, new HoldingFunction<>(stack), defaultValue, min, max);
    }

    public MValueRoundedDouble(MValueCategory category, Identifier id, Function<MValue<Double>, ItemStack> stackFunction, double defaultValue, double min, double max) {
        super(category, id, stackFunction, defaultValue, min, max);
    }

    @Override
    public void sendValue(Double value) {
        super.sendValue(Math.floor(value * 10f) / 10f);
    }
}