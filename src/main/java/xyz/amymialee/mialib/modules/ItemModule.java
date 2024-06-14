package xyz.amymialee.mialib.modules;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import xyz.amymialee.mialib.Mialib;

public interface ItemModule {
    TagKey<Item> SOUL_FIRE_SMELTING = TagKey.of(Registries.ITEM.getKey(), Mialib.id("soul_fire_smelting"));
    TagKey<Item> UNDESTROYABLE = TagKey.of(Registries.ITEM.getKey(), Mialib.id("damage_immune"));
    TagKey<Item> UNCRAFTABLE = TagKey.of(Registries.ITEM.getKey(), Mialib.id("uncraftable"));
    TagKey<Item> UNBREAKABLE = TagKey.of(Registries.ITEM.getKey(), Mialib.id("unbreakable"));

    static void init() {}
}
