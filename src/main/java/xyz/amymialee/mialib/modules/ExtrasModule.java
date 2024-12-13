package xyz.amymialee.mialib.modules;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.mvalues.MValue;

public interface ExtrasModule {
    TagKey<Item> UNDESTROYABLE = TagKey.of(Registries.ITEM.getKey(), Mialib.id("damage_immune"));
    TagKey<Item> UNBREAKABLE = TagKey.of(Registries.ITEM.getKey(), Mialib.id("unbreakable"));

    MValue<Boolean> DISABLE_PIGLIN_PORTAL_SPAWNING = MValue.of(Mialib.id("disable_piglin_portal_spawning"), MValue.BOOLEAN_FALSE).item((v) -> v.get() ? Items.ROTTEN_FLESH : Items.GOLD_NUGGET).build();
    MValue<Boolean> DISABLE_END_PORTALS = MValue.of(Mialib.id("disable_end_portals"), MValue.BOOLEAN_FALSE).item((v) -> v.get() ? Items.END_STONE_BRICK_SLAB : Items.END_PORTAL_FRAME).build();

    static void init() {
    }
}