package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface MEnchantment {
    Identifier mialib$getId();

    void mialib$setId(Identifier id);

    TagKey<Item> mialib$getExtraItems();
}