package xyz.amymialee.mialib.values;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public abstract class MValue<T> {
    private final Text name;
    private final Text tooltip;
    private final ItemStack stack;
    private T value;

    public MValue(Text name, Text tooltip, ItemStack stack) {
        this.name = name;
        this.tooltip = tooltip;
        this.stack = stack;
    }
}