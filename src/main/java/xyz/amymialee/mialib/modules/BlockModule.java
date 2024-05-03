package xyz.amymialee.mialib.modules;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import xyz.amymialee.mialib.MiaLib;

public interface BlockModule {
    TagKey<Block> EMPTY = TagKey.of(Registries.BLOCK.getKey(), MiaLib.id("empty"));

    static void init() {}
}
