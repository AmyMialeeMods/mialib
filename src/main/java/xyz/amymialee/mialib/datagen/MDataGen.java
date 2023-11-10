package xyz.amymialee.mialib.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class MDataGen implements DataGeneratorEntrypoint {
	public static final Model SPAWN_EGG = new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty());

	@Override
	public void onInitializeDataGenerator(@NotNull FabricDataGenerator generator) {
//		generator.addProvider((dataGenerator) -> new MAdvancementProvider(this, dataGenerator));
//		generator.addProvider((dataGenerator) -> new MBlockLootTableProvider(this, dataGenerator));
//		generator.addProvider((dataGenerator) -> new MLanguageProvider(this, dataGenerator));
//		generator.addProvider((dataGenerator) -> new MLootTableProvider(this, dataGenerator));
//		generator.addProvider((dataGenerator) -> new MModelProvider(this, dataGenerator));
//		generator.addProvider((dataGenerator) -> new MRecipeProvider(this, dataGenerator));
//		generator.addProvider((dataGenerator) -> new MBlockTagProvider(this, dataGenerator));
//		generator.addProvider((dataGenerator) -> new MItemTagProvider(this, dataGenerator));
//		generator.addProvider((dataGenerator) -> new MFluidTagProvider(this, dataGenerator));
//		generator.addProvider((dataGenerator) -> new MEntityTypeTagProvider(this, dataGenerator));
//		generator.addProvider((dataGenerator) -> new MGameEventTagProvider(this, dataGenerator));
	}

	protected void generateAdvancements(Consumer<AdvancementEntry> consumer) {}

	protected void generateBlockLootTables() {}

	protected void generateTranslations(FabricLanguageProvider.TranslationBuilder builder) {}

	protected void generateLootTables(BiConsumer<Identifier, LootTable.Builder> consumer) {}

	protected void generateBlockStateModels(BlockStateModelGenerator generator) {}

	protected void generateItemModels(ItemModelGenerator generator) {}

	protected void generateRecipes(RecipeExporter exporter) {}

	protected void generateBlockTags(RegistryWrapper.WrapperLookup arg) {}

	protected void generateItemTags(RegistryWrapper.WrapperLookup arg) {}

	protected void generateFluidTags(RegistryWrapper.WrapperLookup arg) {}

	protected void generateEntityTypeTags(RegistryWrapper.WrapperLookup arg) {}

	protected void generateGameEventTags(RegistryWrapper.WrapperLookup arg) {}

	private static @NotNull Advancement emptyAdvancement(String id) {
		return emptyAdvancement(new Identifier(id));
	}

	private static @NotNull Advancement emptyAdvancement(Identifier id) {
		return new Advancement(Optional.of(id), Optional.empty(), null, Map.of(), null, false);
	}

	private static LootTable.Builder makeItemWithRange(ItemConvertible item, int min, int max) {
		return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max)))));
	}

	private static class MAdvancementProvider extends FabricAdvancementProvider {
		private final MDataGen dataGen;

		public MAdvancementProvider(MDataGen gen, FabricDataOutput output) {
			super(output);
			this.dataGen = gen;
		}

		@Override
		public void generateAdvancement(Consumer<AdvancementEntry> consumer) {
			this.dataGen.generateAdvancements(consumer);
		}
	}

	private static class MBlockLootTableProvider extends FabricBlockLootTableProvider {
		private final MDataGen dataGen;

		public MBlockLootTableProvider(MDataGen gen, FabricDataOutput output) {
			super(output);
			this.dataGen = gen;
		}

		@Override
		public void generate() {
			this.dataGen.generateBlockLootTables();
		}
	}

	private static class MLanguageProvider extends FabricLanguageProvider {
		private final MDataGen dataGen;

		public MLanguageProvider(MDataGen gen, FabricDataOutput output) {
			super(output, "en_us");
			this.dataGen = gen;
		}

		@Override
		public void generateTranslations(TranslationBuilder builder) {
			this.dataGen.generateTranslations(builder);
		}
	}

	private static class MLootTableProvider extends SimpleFabricLootTableProvider {
		private final MDataGen dataGen;

		public MLootTableProvider(MDataGen dataGen, FabricDataOutput output) {
			super(output, LootTable.GENERIC);
			this.dataGen = dataGen;
		}

		@Override
		public void accept(BiConsumer<Identifier, LootTable.Builder> consumer) {
			this.dataGen.generateLootTables(consumer);
		}
	}

	private static class MModelProvider extends FabricModelProvider {
		private final MDataGen dataGen;

		public MModelProvider(MDataGen gen, FabricDataOutput output) {
			super(output);
			this.dataGen = gen;
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator generator) {
			this.dataGen.generateBlockStateModels(generator);
		}

		@Override
		public void generateItemModels(ItemModelGenerator generator) {
			this.dataGen.generateItemModels(generator);
		}
	}

	private static class MRecipeProvider extends FabricRecipeProvider {
		private final MDataGen dataGen;

		public MRecipeProvider(MDataGen gen, FabricDataOutput output) {
			super(output);
			this.dataGen = gen;
		}

		@Override
		public void generate(RecipeExporter exporter) {
			this.dataGen.generateRecipes(exporter);
		}
	}

	private static class MBlockTagProvider extends FabricTagProvider.BlockTagProvider {
		private final MDataGen dataGen;

		public MBlockTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateBlockTags(arg);
		}
	}

	private static class MItemTagProvider extends FabricTagProvider.ItemTagProvider {
		private final MDataGen dataGen;

		public MItemTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateItemTags(arg);
		}
	}

	private static class MFluidTagProvider extends FabricTagProvider.FluidTagProvider {
		private final MDataGen dataGen;

		public MFluidTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateFluidTags(arg);
		}
	}

	private static class MEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
		private final MDataGen dataGen;

		public MEntityTypeTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateEntityTypeTags(arg);
		}
	}

	private static class MGameEventTagProvider extends FabricTagProvider.GameEventTagProvider {
		private final MDataGen dataGen;

		public MGameEventTagProvider(MDataGen gen, FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			this.dataGen.generateGameEventTags(arg);
		}
	}
}
