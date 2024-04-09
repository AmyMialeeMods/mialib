package xyz.amymialee.mialib.util.runnables.mvalues;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.mvalues.MValue;

import java.util.function.Function;

public class MBooleanFunction implements Function<MValue<Boolean>, ItemStack> {
    private final ItemStack enabled;
    private final ItemStack disabled;

    public MBooleanFunction(ItemStack enabled, ItemStack disabled) {
        this.enabled = enabled;
        this.disabled = disabled;
    }

    @Override
    public ItemStack apply(@NotNull MValue<Boolean> mValueBoolean) {
        return mValueBoolean.getValue() ? this.enabled : this.disabled;
    }
}