package xyz.amymialee.mialib.mvalues;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import xyz.amymialee.mialib.util.runnables.HoldingFunction;

import java.util.function.Function;

public class MValueRoundedFloat extends MValue.MValueFloat {
    public MValueRoundedFloat(MValueCategory category, Identifier id, ItemStack stack, float defaultValue, float min, float max) {
        super(category, id, new HoldingFunction<>(stack), defaultValue, min, max);
    }

    public MValueRoundedFloat(MValueCategory category, Identifier id, Function<MValue<Float>, ItemStack> stackFunction, float defaultValue, float min, float max) {
        super(category, id, stackFunction, defaultValue, min, max);
    }

    @Override
    public void sendValue(Float value) {
        super.sendValue(MathHelper.floor(value * 10f) / 10f);
    }
}