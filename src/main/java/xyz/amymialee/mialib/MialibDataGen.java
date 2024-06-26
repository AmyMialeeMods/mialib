package xyz.amymialee.mialib;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.modules.ItemModule;
import xyz.amymialee.mialib.templates.MDataGen;

import java.util.function.Consumer;

public class MialibDataGen extends MDataGen {
    public static final RegistryKey<FlatLevelGeneratorPreset> DEV_READY = RegistryKey.of(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, Mialib.id("dev_ready"));
    public static final RegistryKey<FlatLevelGeneratorPreset> BLAST_PROOF = RegistryKey.of(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, Mialib.id("blast_proof"));

    @Override
    protected void generateTranslations(@NotNull MLanguageProvider provider, RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.@NotNull TranslationBuilder builder) {
        builder.add("flat_world_preset." + DEV_READY.getValue().toTranslationKey(), "Dev Ready");
        builder.add("flat_world_preset." + BLAST_PROOF.getValue().toTranslationKey(), "Blast Proof");
        builder.add(provider.getTagTranslationKey(ItemModule.SOUL_FIRE_SMELTING), "Soul Fire Smelting");
        builder.add(provider.getTagTranslationKey(ItemModule.UNDESTROYABLE), "Undestroyable");
        builder.add(provider.getTagTranslationKey(ItemModule.UNCRAFTABLE), "Uncraftable");
        builder.add(provider.getTagTranslationKey(ItemModule.UNBREAKABLE), "Unbreakable");
        for (var single : new Pair[]{new Pair<>("self", "self"), new Pair<>("single", "%s entity"), new Pair<>("multiple", "%s entities")}) {
            for (var enabled : new Pair[]{new Pair<>("enabled", "§aenabled"), new Pair<>("disabled", "§cdisabled")}) {
                builder.add("commands.%s.vanish.%s.%s".formatted(Mialib.MOD_ID, enabled.getLeft(), single.getLeft()), "§7Vanish %s§7 for %s".formatted(enabled.getRight(), single.getRight()));
                builder.add("commands.%s.indestructible.%s.%s".formatted(Mialib.MOD_ID, enabled.getLeft(), single.getLeft()), "§7Indestructibility %s§7 for %s".formatted(enabled.getRight(), single.getRight()));
                builder.add("commands.%s.immortal.%s.%s".formatted(Mialib.MOD_ID, enabled.getLeft(), single.getLeft()), "§7Immortality %s§7 for %s".formatted(enabled.getRight(), single.getRight()));
            }
        }
        builder.add("%s.screen.mvalues".formatted(Mialib.MOD_ID), "%s Value Editor".formatted(Mialib.MOD_NAME));
        builder.add("%s.screen.mvalues.page".formatted(Mialib.MOD_ID), "Page %2d/%2d");
        builder.add(Mialib.MIALIB_CATEGORY.getTranslationKey(), Mialib.MOD_NAME);
        builder.add(Mialib.CREATIVE_NO_SLEEP.getTranslationKey(), "Creative No Sleep");
        builder.add(Mialib.CREATIVE_NO_SLEEP.getDescriptionTranslationKey(), "Creative players don't need to sleep to skip the night");
        builder.add(Mialib.DISABLE_PIGLIN_PORTAL_SPAWNING.getTranslationKey(), "Disable Piglin Spawning");
        builder.add(Mialib.DISABLE_PIGLIN_PORTAL_SPAWNING.getDescriptionTranslationKey(), "Disables piglins spawning from nether portals");
        builder.add(Mialib.DISABLE_NETHER_PORTALS.getTranslationKey(), "Disable Nether Portals");
        builder.add(Mialib.DISABLE_NETHER_PORTALS.getDescriptionTranslationKey(), "Disables nether portals");
        builder.add(Mialib.DISABLE_END_PORTALS.getTranslationKey(), "Disable End Portals");
        builder.add(Mialib.DISABLE_END_PORTALS.getDescriptionTranslationKey(), "Disables end portals");
        builder.add("category.%s".formatted(Mialib.MOD_ID), Mialib.MOD_NAME);
        builder.add("key.%s.mvalues".formatted(Mialib.MOD_ID), "Open %s Value Editor".formatted(Mialib.MOD_NAME));
        builder.add("%s.servers".formatted(Mialib.MOD_ID), "Mialib Servers");
        builder.add("%s.mialib_server.true".formatted(Mialib.MOD_ID), "Save to Mialib Server File: §aTrue");
        builder.add("%s.mialib_server.false".formatted(Mialib.MOD_ID), "Save to Mialib Server File: §cFalse");
        builder.add("commands.mvalue.query", "MValue %s is currently set to: %s");
        builder.add("commands.mvalue.set", "MValue %s is now set to: %s");
    }

    @Override
    protected void generateItemTags(MDataGen.@NotNull MItemTagProvider provider, RegistryWrapper.WrapperLookup arg) {
        provider.getOrCreateTagBuilder(ItemModule.SOUL_FIRE_SMELTING)
                .add(Items.ANCIENT_DEBRIS, Items.NETHERITE_SCRAP, Items.NETHERITE_INGOT, Items.NETHERITE_BLOCK)
                .add(Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE)
                .add(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS)
                .add(Items.SOUL_CAMPFIRE, Items.SOUL_LANTERN, Items.SOUL_TORCH)
                .add(Items.SOUL_SAND, Items.SOUL_SOIL);
        provider.getOrCreateTagBuilder(ItemModule.UNDESTROYABLE);
        provider.getOrCreateTagBuilder(ItemModule.UNCRAFTABLE);
        provider.getOrCreateTagBuilder(ItemModule.UNBREAKABLE);
    }

    @Override
    protected void generateFlatLevelGeneratorPresetTags(@NotNull MFlatLevelGeneratorPresetTagProvider provider, RegistryWrapper.WrapperLookup arg) {
        provider.getOrCreateTagBuilder(FlatLevelGeneratorPresetTags.VISIBLE).setReplace(false)
                .addOptional(DEV_READY.getValue()).addOptional(BLAST_PROOF.getValue());
    }

    @Override
    protected void generateFlatLevelGeneratorPresets(MFlatLevelGeneratorPresetProvider provider, @NotNull Consumer<FlatLevelGeneratorPresetData> consumer) {
        consumer.accept(new FlatLevelGeneratorPresetData(DEV_READY, Items.AMETHYST_SHARD, BiomeKeys.DARK_FOREST, ImmutableSet.of(), false, false, new FlatChunkGeneratorLayer(15, Blocks.RED_SANDSTONE), new FlatChunkGeneratorLayer(112, Blocks.DEEPSLATE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)));
        consumer.accept(new FlatLevelGeneratorPresetData(BLAST_PROOF, Items.TNT, BiomeKeys.DARK_FOREST, ImmutableSet.of(), false, false, new FlatChunkGeneratorLayer(15, Blocks.OBSIDIAN), new FlatChunkGeneratorLayer(112, Blocks.REINFORCED_DEEPSLATE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)));
    }
}