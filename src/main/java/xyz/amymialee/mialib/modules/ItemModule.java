package xyz.amymialee.mialib.modules;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import xyz.amymialee.mialib.MiaLib;

public interface ItemModule {
    TagKey<Item> SOUL_FIRE_SMELTING = TagKey.of(Registries.ITEM.getKey(), MiaLib.id("soul_fire_smelting"));
    TagKey<Item> UNDESTROYABLE = TagKey.of(Registries.ITEM.getKey(), MiaLib.id("damage_immune"));
    TagKey<Item> UNCRAFTABLE = TagKey.of(Registries.ITEM.getKey(), MiaLib.id("uncraftable"));
    TagKey<Item> UNBREAKABLE = TagKey.of(Registries.ITEM.getKey(), MiaLib.id("unbreakable"));

    static void init() {}
}
