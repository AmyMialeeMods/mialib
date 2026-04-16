package xyz.amymialee.mialib.templates;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Fabric Data Generator with many fabric data providers pre-included.
 * Any overridden methods will run their respective provider.
 */
public @SuppressWarnings("unused") abstract class MDataGen implements DataGeneratorEntrypoint {
	private String name;

	@Override
	public void onInitializeDataGenerator(@NotNull FabricDataGenerator generator) {
        this.name = generator.getModId();
		var pack = generator.createPack();
		var set = new HashSet<String>();
		for (var method : this.getClass().getDeclaredMethods()) set.add(method.getName());
		if (set.contains("generateTranslations")) pack.addProvider((dataOutput, future) -> new MLanguageProvider(this, dataOutput, future));
		if (set.contains("generateItemModels")) pack.addProvider((dataOutput, future) -> new MModelProvider(this, dataOutput));
		if (set.contains("generateRecipes")) pack.addProvider((dataOutput, future) -> new MRecipeProvider(this, dataOutput, future));
		if (set.contains("addExtraDataProviders")) this.addExtraDataProviders(pack);
	}

	protected void generateTranslations(MLanguageProvider provider, HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder builder) {}

	protected void generateItemModels(MModelProvider provider, ItemModelGenerators generator) {}

	protected void generateRecipes(MRecipeProvider provider, RecipeProvider recipeGenerator, HolderLookup.Provider registryLookup, RecipeOutput exporter) {}

	protected void addExtraDataProviders(FabricDataGenerator.Pack pack) {}

	protected static class MLanguageProvider extends FabricLanguageProvider {
		private final MDataGen dataGen;

		public MLanguageProvider(MDataGen gen, FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(packOutput, registryLookup);
			this.dataGen = gen;
		}

		@Override
		public void generateTranslations(HolderLookup.@NonNull Provider registryLookup, @NonNull TranslationBuilder translationBuilder) {
			this.dataGen.generateTranslations(this, registryLookup, translationBuilder);
		}

		public String getTagTranslationKey(@NotNull TagKey<?> key) {
			return this.translatePrefix("tag." + key.registry().identifier().getPath() + ".", key.location());
		}

		public String translatePrefix(String prefix, @NotNull Identifier id) {
			return prefix + id.getNamespace() + "." + id.getPath().replace('/', '.');
		}

		@Override
		public @NonNull String getName() {
			return this.dataGen.name + " " + super.getName();
		}
	}

	protected static class MModelProvider extends FabricModelProvider {
		private final MDataGen dataGen;

		public MModelProvider(MDataGen gen, FabricPackOutput output) {
			super(output);
			this.dataGen = gen;
		}

		@Override
		public void generateBlockStateModels(@NonNull BlockModelGenerators generator) {}

		@Override
		public void generateItemModels(@NonNull ItemModelGenerators generator) {
			this.dataGen.generateItemModels(this, generator);
		}

		@Override
		public @NonNull String getName() {
			return this.dataGen.name + " " + super.getName();
		}
	}

	protected static class MRecipeProvider extends FabricRecipeProvider {
		private final MDataGen dataGen;

		public MRecipeProvider(MDataGen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
			this.dataGen = gen;
		}

		@Override
		protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registryLookup, @NonNull RecipeOutput exporter) {
			return new RecipeProvider(registryLookup, exporter) {
				@Override
				public void buildRecipes() {
					MRecipeProvider.this.dataGen.generateRecipes(MRecipeProvider.this, this, this.registries, this.output);
				}
			};
		}

		@Override
		public @NonNull String getName() {
			return this.dataGen.name + " Recipes";
		}
	}
}