package xyz.amymialee.mialib.templates;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Fabric Data Generator with all many fabric data providers pre-included.
 */
@SuppressWarnings({"unused", "EmptyMethod"})
public abstract class MDataGen implements DataGeneratorEntrypoint {
	private static boolean initialized = false;

	@Override
	public void onInitializeDataGenerator(@NotNull FabricDataGenerator generator) {
		if (!initialized) {
			MRegistry.tryBuildAll("%s Datagen".formatted(generator.getModId()));
			initialized = true;
		}
		var pack = generator.createPack();
		pack.addProvider((dataOutput, future) -> new MAdvancementProvider(this, dataOutput));
		pack.addProvider((dataOutput, future) -> new MBlockLootTableProvider(this, dataOutput));
		pack.addProvider((dataOutput, future) -> new MLanguageProvider(this, dataOutput));
		pack.addProvider((dataOutput, future) -> new MLootTableProvider(this, dataOutput));
		pack.addProvider((dataOutput, future) -> new MModelProvider(this, dataOutput));
		pack.addProvider((dataOutput, future) -> new MRecipeProvider(this, dataOutput));
		pack.addProvider((dataOutput, future) -> new MFlatLevelGeneratorPresetProvider(this, dataOutput, future));
		/* Tag Providers */
		pack.addProvider((dataOutput, future) -> new MBlockTagProvider(this, dataOutput, future));
		pack.addProvider((dataOutput, future) -> new MItemTagProvider(this, dataOutput, future));
		pack.addProvider((dataOutput, future) -> new MFluidTagProvider(this, dataOutput, future));
		pack.addProvider((dataOutput, future) -> new MEntityTypeTagProvider(this, dataOutput, future));
		pack.addProvider((dataOutput, future) -> new MGameEventTagProvider(this, dataOutput, future));
		pack.addProvider((dataOutput, future) -> new MDamageTypeTagProvider(this, dataOutput, future));
		pack.addProvider((dataOutput, future) -> new MFlatLevelGeneratorPresetTagProvider(this, dataOutput, future));
		this.addExtraDataProviders(pack);
	}

	protected void addExtraDataProviders(FabricDataGenerator.Pack pack) {}

	protected void generateAdvancements(MAdvancementProvider provider, Consumer<Advancement> consumer) {}

	protected void generateBlockLootTables(MBlockLootTableProvider provider) {}

	protected void generateTranslations(MLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {}

	protected void generateLootTables(MLootTableProvider provider, BiConsumer<Identifier, LootTable.Builder> consumer) {}

	protected void generateBlockStateModels(MModelProvider provider, BlockStateModelGenerator generator) {}

	protected void generateItemModels(MModelProvider provider, ItemModelGenerator generator) {}

	protected void generateRecipes(MRecipeProvider provider, Consumer<RecipeJsonProvider> exporter) {}

	protected void generateFlatLevelGeneratorPresets(MFlatLevelGeneratorPresetProvider provider, Consumer<FlatLevelGeneratorPresetData> consumer) {}

	protected void generateBlockTags(MBlockTagProvider provider, RegistryWrapper.WrapperLookup arg) {}

	protected void generateItemTags(MItemTagProvider provider, RegistryWrapper.WrapperLookup arg) {}

	protected void generateFluidTags(MFluidTagProvider provider, RegistryWrapper.WrapperLookup arg) {}

	protected void generateEntityTypeTags(MEntityTypeTagProvider provider, RegistryWrapper.WrapperLookup arg) {}

	protected void generateGameEventTags(MGameEventTagProvider provider, RegistryWrapper.WrapperLookup arg) {}

	protected void generateDamageTypeTags(MDamageTypeTagProvider provider, RegistryWrapper.WrapperLookup arg) {}

	protected void generateFlatLevelGeneratorPresetTags(MFlatLevelGeneratorPresetTagProvider provider, RegistryWrapper.WrapperLookup arg) {}

	protected static class MAdvancementProvider extends FabricAdvancementProvider {
		private final MDataGen dataGen;

		public MAdvancementProvider(MDataGen gen, FabricDataOutput output) {
			super(output);
			this.dataGen = gen;
		}

		@Override
		public void generateAdvancement(Consumer<Advancement> consumer) {
			this.dataGen.generateAdvancements(this, consumer);
		}

		public @NotNull Advancement emptyAdvancement(String id) {
			return this.emptyAdvancement(new Identifier(id));
		}

		public @NotNull Advancement emptyAdvancement(Identifier id) {
			return new Advancement(id, null, null, AdvancementRewards.NONE, Map.of(), new String[0][0], false);
		}
	}

	protected static class MBlockLootTableProvider extends FabricBlockLootTableProvider {
		private final MDataGen dataGen;

		public MBlockLootTableProvider(MDataGen gen, FabricDataOutput output) {
			super(output);
			this.dataGen = gen;
		}

		@Override
		public void generate() {
			this.dataGen.generateBlockLootTables(this);
		}

		public LootTable.Builder makeItemWithRange(ItemConvertible item, int min, int max) {
			return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max)))));
		}
	}

	protected static class MLanguageProvider extends FabricLanguageProvider {
		private final MDataGen dataGen;

		public MLanguageProvider(MDataGen gen, FabricDataOutput output) {
			super(output, "en_us");
			this.dataGen = gen;
		}

		@Override
		public void generateTranslations(TranslationBuilder builder) {
			this.dataGen.generateTranslations(this, builder);
		}

		public String getTagTranslationKey(@NotNull TagKey<?> key) {
			return this.translatePrefix("tag." + key.registry().getValue().getPath() + ".", key.id());
		}

		public String translatePrefix(String prefix, @NotNull Identifier id) {
			return prefix + id.getNamespace() + "." + id.getPath().replace('/', '.');
		}

		public String getSubtitleKey(@NotNull SoundEvent event) {
			return "subtitles." + event.getId().getNamespace() + "." + event.getId().getPath();
		}

		public String[] getDamageKeys(@NotNull Identifier damageName) {
			return this.getDamageKeys(damageName.getPath());
		}

		public String[] getDamageKeys(String damageName) {
			return new String[] {
					"death.attack." + damageName,
					"death.attack." + damageName + ".item",
					"death.attack." + damageName + ".player"
			};
		}
	}

	protected static class MLootTableProvider extends SimpleFabricLootTableProvider {
		private final MDataGen dataGen;

		public MLootTableProvider(MDataGen dataGen, FabricDataOutput output) {
			super(output, LootTable.GENERIC);
			this.dataGen = dataGen;
		}

		@Override
		public void accept(BiConsumer<Identifier, LootTable.Builder> consumer) {
			this.dataGen.generateLootTables(this, consumer);
		}

		public LootTable.Builder makeItemWithRange(ItemConvertible item, int min, int max) {
			return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max)))));
		}
	}

	protected static class MModelProvider extends FabricModelProvider {
		public static final Model SPAWN_EGG = new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty());
		private final MDataGen dataGen;

		public MModelProvider(MDataGen gen, FabricDataOutput output) {
			super(output);
			this.dataGen = gen;
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator generator) {
			this.dataGen.generateBlockStateModels(this, generator);
		}

		@Override
		public void generateItemModels(ItemModelGenerator generator) {
			this.dataGen.generateItemModels(this, generator);
		}
	}

	protected static class MRecipeProvider extends FabricRecipeProvider {
		private final MDataGen dataGen;

		public MRecipeProvider(MDataGen gen, FabricDataOutput output) {
			super(output);
			this.dataGen = gen;
		}

		@Override
		public void generate(Consumer<RecipeJsonProvider> exporter) {
			this.dataGen.generateRecipes(this, exporter);
		}
	}

	protected static class MBlockTagProvider extends FabricTagProvider.BlockTagProvider {
		private final MDataGen dataGen;

		public MBlockTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateBlockTags(this, arg);
		}

		@Override
		public FabricTagBuilder getOrCreateTagBuilder(TagKey<Block> tag) {
			return super.getOrCreateTagBuilder(tag);
		}
	}

	protected static class MItemTagProvider extends FabricTagProvider.ItemTagProvider {
		private final MDataGen dataGen;

		public MItemTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateItemTags(this, arg);
		}

		@Override
		public FabricTagBuilder getOrCreateTagBuilder(TagKey<Item> tag) {
			return super.getOrCreateTagBuilder(tag);
		}
	}

	protected static class MFluidTagProvider extends FabricTagProvider.FluidTagProvider {
		private final MDataGen dataGen;

		public MFluidTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateFluidTags(this, arg);
		}

		@Override
		public FabricTagBuilder getOrCreateTagBuilder(TagKey<Fluid> tag) {
			return super.getOrCreateTagBuilder(tag);
		}
	}

	protected static class MEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
		private final MDataGen dataGen;

		public MEntityTypeTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateEntityTypeTags(this, arg);
		}

		@Override
		public FabricTagProvider<EntityType<?>>.FabricTagBuilder getOrCreateTagBuilder(TagKey<EntityType<?>> tag) {
			return super.getOrCreateTagBuilder(tag);
		}
	}

	protected static class MGameEventTagProvider extends FabricTagProvider.GameEventTagProvider {
		private final MDataGen dataGen;

		public MGameEventTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateGameEventTags(this, arg);
		}

		@Override
		public FabricTagBuilder getOrCreateTagBuilder(TagKey<GameEvent> tag) {
			return super.getOrCreateTagBuilder(tag);
		}
	}

	protected static class MDamageTypeTagProvider extends FabricTagProvider<DamageType> {
		private final MDataGen dataGen;

		public MDamageTypeTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(output, RegistryKeys.DAMAGE_TYPE, completableFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateDamageTypeTags(this, arg);
		}

		@Override
		public FabricTagBuilder getOrCreateTagBuilder(TagKey<DamageType> tag) {
			return super.getOrCreateTagBuilder(tag);
		}
	}

	protected static class MFlatLevelGeneratorPresetTagProvider extends FabricTagProvider<FlatLevelGeneratorPreset> {
		private final MDataGen dataGen;

		public MFlatLevelGeneratorPresetTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(output, RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, completableFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateFlatLevelGeneratorPresetTags(this, arg);
		}

		@Override
		public FabricTagBuilder getOrCreateTagBuilder(TagKey<FlatLevelGeneratorPreset> tag) {
			return super.getOrCreateTagBuilder(tag);
		}
	}

	protected static class MFlatLevelGeneratorPresetProvider implements DataProvider {
		private final MDataGen dataGen;
		private final DataOutput.PathResolver pathResolver;
        private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

		public MFlatLevelGeneratorPresetProvider(MDataGen gen, @NotNull FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			this.dataGen = gen;
            this.registriesFuture = registriesFuture;
			this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "worldgen/flat_level_generator_preset");
		}

		public void generate(Consumer<FlatLevelGeneratorPresetData> consumer) {
			this.dataGen.generateFlatLevelGeneratorPresets(this, consumer);
		}

		@SuppressWarnings("UnnecessarilyQualifiedStaticUsage")
        @Override
		public CompletableFuture<?> run(DataWriter writer) {
			return this.registriesFuture.thenCompose(lookup -> {
				Set<Identifier> set = new HashSet<>();
				List<CompletableFuture<?>> list = new ArrayList<>();
				Consumer<FlatLevelGeneratorPresetData> consumer = flatPreset -> {
					if (!set.add(flatPreset.name.getValue())) {
						throw new IllegalStateException("Duplicate Superflat Preset " + flatPreset.name.getValue());
					} else {
						var path = this.pathResolver.resolveJson(flatPreset.name.getValue());
						list.add(DataProvider.writeToPath(writer, flatPreset.toJson(), path));
					}
				};
				this.generate(consumer);
				return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
			});
		}

		@Override
		public String getName() {
			return "Flat Level Generator Presets";
		}
	}

	public static class FlatLevelGeneratorPresetData {
		protected final RegistryKey<FlatLevelGeneratorPreset> name;
		protected final ItemConvertible icon;
		protected final RegistryKey<Biome> biome;
		protected final Set<RegistryKey<StructureSet>> structureSetKeys;
		protected final boolean hasFeatures;
		protected final boolean hasLakes;
		protected final FlatChunkGeneratorLayer[] layers;

		public FlatLevelGeneratorPresetData(
				RegistryKey<FlatLevelGeneratorPreset> registryKey,
				ItemConvertible icon,
				RegistryKey<Biome> biome,
				@NotNull Set<RegistryKey<StructureSet>> structureSetKeys,
				boolean hasFeatures,
				boolean hasLakes,
				FlatChunkGeneratorLayer... layers) {
			this.name = registryKey;
			this.icon = icon;
			this.biome = biome;
			this.structureSetKeys = structureSetKeys;
			this.hasFeatures = hasFeatures;
			this.hasLakes = hasLakes;
			this.layers = layers;
		}

		public JsonElement toJson() {
			var base = new JsonObject();
			base.addProperty("display", this.icon.asItem().toString());
			var settings = new JsonObject();
			settings.addProperty("biome", this.biome.getValue().toString());
			settings.addProperty("features", this.hasFeatures);
			settings.addProperty("lakes", this.hasLakes);
			var layersJson = new JsonArray();
			for(var i = this.layers.length - 1; i >= 0; i--) {
				var layerInstance = this.layers[i];
				var layerJson = new JsonObject();
				layerJson.addProperty("block", Registries.BLOCK.getId(layerInstance.getBlockState().getBlock()).toString());
				layerJson.addProperty("height", layerInstance.getThickness());
				layersJson.add(layerJson);
			}
			settings.add("layers", layersJson);
			var structuresJson = new JsonArray();
			for (var structureInstance : this.structureSetKeys) {
				structuresJson.add(structureInstance.getValue().toString());
			}
			settings.add("structure_overrides", structuresJson);
			base.add("settings", settings);
			return base;
		}
	}
}