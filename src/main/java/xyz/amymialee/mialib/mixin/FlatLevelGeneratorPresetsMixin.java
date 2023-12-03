package xyz.amymialee.mialib.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.StructureSet;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.MiaLib;

import java.util.Set;

@Mixin(FlatLevelGeneratorPresets.class)
public abstract class FlatLevelGeneratorPresetsMixin {

    @Mixin(FlatLevelGeneratorPresets.Registrar.class)
    static abstract class RegistrarMixin {
        @Shadow protected abstract void createAndRegister(RegistryKey<FlatLevelGeneratorPreset> registryKey, ItemConvertible icon, RegistryKey<Biome> biome, Set<RegistryKey<StructureSet>> structureSetKeys, boolean hasFeatures, boolean hasLakes, FlatChunkGeneratorLayer... layers);

        @Inject(method = "bootstrap", at = @At("HEAD"))
        private void mialib$addPresets(CallbackInfo ci) {
            this.createAndRegister(MiaLib.DEV_READY, Items.AMETHYST_SHARD, BiomeKeys.DARK_FOREST, ImmutableSet.of(), false, false, new FlatChunkGeneratorLayer(15, Blocks.RED_SANDSTONE), new FlatChunkGeneratorLayer(112, Blocks.DEEPSLATE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
            this.createAndRegister(MiaLib.BLAST_PROOF, Items.TNT, BiomeKeys.DARK_FOREST, ImmutableSet.of(), false, false, new FlatChunkGeneratorLayer(15, Blocks.OBSIDIAN), new FlatChunkGeneratorLayer(112, Blocks.REINFORCED_DEEPSLATE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        }
    }
}