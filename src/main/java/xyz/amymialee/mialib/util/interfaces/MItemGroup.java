package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.item.ItemGroup;

@SuppressWarnings("unused")
public interface MItemGroup {
    default boolean mialib$hasConstantIcon() {
        return false;
    }

    default ItemGroup mialib$setConstantIcon(boolean constantIcon) {
        return null;
    }
}