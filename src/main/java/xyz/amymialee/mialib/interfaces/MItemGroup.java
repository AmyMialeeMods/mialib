package xyz.amymialee.mialib.interfaces;

import net.minecraft.item.ItemGroup;

public interface MItemGroup {
    default boolean mialib$hasConstantIcon() {
        return false;
    }

    default ItemGroup mialib$setConstantIcon(boolean constantIcon) {
        return null;
    }
}