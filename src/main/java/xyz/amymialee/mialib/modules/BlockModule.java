package xyz.amymialee.mialib.modules;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import xyz.amymialee.mialib.Mialib;

public interface BlockModule {
    TagKey<Block> EMPTY = TagKey.of(Registries.BLOCK.getKey(), Mialib.id("empty"));

    static void init() {}
}
