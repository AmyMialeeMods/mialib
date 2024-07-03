package xyz.amymialee.mialib.api;

import net.minecraft.item.ItemStack;

/**
 * {@link net.minecraft.item.Item Items} can inherit this interface to specify whether they can be crafted or not.
 */
public interface Craftability {

    /**
     * @param stack The {@link ItemStack} to check.
     * @return Whether the {@link ItemStack} can be crafted.
     */
    boolean canBeCrafted(ItemStack stack);

}
