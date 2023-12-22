package xyz.amymialee.mialib;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.data.MDataGen;

import java.util.function.Consumer;

public class MiaLibDataGen extends MDataGen {
    @Override
    protected void generateTranslations(@NotNull MLanguageProvider provider, FabricLanguageProvider.@NotNull TranslationBuilder builder) {
        builder.add(MiaLib.FIRE_ASPECT_AUTOSMELTING.getTranslationKey(), "Fire Aspect Autosmelting");
        builder.add("flat_world_preset." + MiaLib.DEV_READY.getValue().toTranslationKey(), "Dev Ready");
        builder.add("flat_world_preset." + MiaLib.BLAST_PROOF.getValue().toTranslationKey(), "Blast Proof");
        builder.add(provider.getTagTranslationKey(MiaLib.SOUL_FIRE_SMELTING), "Soul Fire Smelting");
        builder.add(provider.getTagTranslationKey(MiaLib.UNDESTROYABLE), "Undestroyable");
        builder.add(provider.getTagTranslationKey(MiaLib.UNCRAFTABLE), "Uncraftable");
        builder.add(provider.getTagTranslationKey(MiaLib.UNBREAKABLE), "Unbreakable");
        for (var enabled : new boolean[]{true, false}) {
            for (var single : new boolean[]{true, false}) {
                builder.add("commands.mialib.vanish." + (enabled ? "enabled" : "disabled") + "." + (single ? "single" : "multiple"), "§7Vanish " + (enabled ? "§aenabled" : "§cdisabled") + "§7 for %s");
                builder.add("commands.mialib.indestructible." + (enabled ? "enabled" : "disabled") + "." + (single ? "single" : "multiple"), "§7Indestructibility " + (enabled ? "§aenabled" : "§cdisabled") + "§7 for %s");
            }
            builder.add("commands.mialib.vanish." + (enabled ? "enabled" : "disabled") + ".self", "§7Vanish " + (enabled ? "§aenabled" : "§cdisabled") + "§7 for self");
            builder.add("commands.mialib.indestructible." + (enabled ? "enabled" : "disabled") + ".self", "§7Indestructibility " + (enabled ? "§aenabled" : "§cdisabled") + "§7 for self");
        }
    }

    @Override
    protected void generateItemTags(MDataGen.@NotNull MItemTagProvider provider, RegistryWrapper.WrapperLookup arg) {
        provider.getOrCreateTagBuilder(MiaLib.SOUL_FIRE_SMELTING)
                .add(Items.ANCIENT_DEBRIS, Items.NETHERITE_SCRAP, Items.NETHERITE_INGOT, Items.NETHERITE_BLOCK)
                .add(Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE)
                .add(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS)
                .add(Items.SOUL_CAMPFIRE, Items.SOUL_LANTERN, Items.SOUL_TORCH)
                .add(Items.SOUL_SAND, Items.SOUL_SOIL);
        provider.getOrCreateTagBuilder(MiaLib.UNDESTROYABLE);
        provider.getOrCreateTagBuilder(MiaLib.UNCRAFTABLE);
        provider.getOrCreateTagBuilder(MiaLib.UNBREAKABLE);
    }

    @Override
    protected void generateFlatLevelGeneratorPresetTags(@NotNull MFlatLevelGeneratorPresetTagProvider provider, RegistryWrapper.WrapperLookup arg) {
        provider.getOrCreateTagBuilder(FlatLevelGeneratorPresetTags.VISIBLE).setReplace(false)
                .addOptional(MiaLib.DEV_READY.getValue()).addOptional(MiaLib.BLAST_PROOF.getValue());
    }

    @Override
    protected void generateFlatLevelGeneratorPresets(MFlatLevelGeneratorPresetProvider provider, @NotNull Consumer<FlatLevelGeneratorPresetData> consumer) {
        consumer.accept(new FlatLevelGeneratorPresetData(MiaLib.DEV_READY, Items.AMETHYST_SHARD, BiomeKeys.DARK_FOREST, ImmutableSet.of(), false, false, new FlatChunkGeneratorLayer(15, Blocks.RED_SANDSTONE), new FlatChunkGeneratorLayer(112, Blocks.DEEPSLATE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)));
        consumer.accept(new FlatLevelGeneratorPresetData(MiaLib.BLAST_PROOF, Items.TNT, BiomeKeys.DARK_FOREST, ImmutableSet.of(), false, false, new FlatChunkGeneratorLayer(15, Blocks.OBSIDIAN), new FlatChunkGeneratorLayer(112, Blocks.REINFORCED_DEEPSLATE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)));
    }
}