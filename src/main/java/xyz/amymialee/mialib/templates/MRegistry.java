package xyz.amymialee.mialib.templates;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.score.LootScoreProviderType;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.scoreboard.number.NumberFormatType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSourceType;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.minecraft.world.gen.root.RootPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialib.Mialib;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public @SuppressWarnings({"unused", "UnusedReturnValue"}) class MRegistry {
	private static final Map<Class<?>, Registry<?>> DEFAULT_REGISTRIES = new HashMap<>();
	private final Map<Class<?>, Registry<?>> registries = new HashMap<>();
	private final String namespace;
	
	public MRegistry(String namespace) {
		this.namespace = namespace;
		this.registries.putAll(DEFAULT_REGISTRIES);
	}
	
	public void clearRegistries() {
		this.registries.clear();
	}
	
	public void addRegistry(Class<?> clazz, Registry<?> registry) {
		this.registries.put(clazz, registry);
	}

	public @SafeVarargs final Block register(String path, AbstractBlock.@NotNull Settings settings, @NotNull Function<AbstractBlock.Settings, Block> function, RegistryKey<ItemGroup> @NotNull ... groups) {
		var key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(this.namespace, path));
		var block = function.apply(settings.registryKey(key));
		Registry.register(Registries.BLOCK, key, block);
		if (groups.length > 0) {
			var item = this.register(path, new Item.Settings(), (s) -> new BlockItem(block, s), groups);
			Item.BLOCK_ITEMS.put(block, item);
			for (var group : groups) this.addToItemGroup(group, item.getDefaultStack());
		}
		return block;
	}

	public @SafeVarargs final Item register(String path, Item.@NotNull Settings settings, @NotNull Function<Item.Settings, Item> function, RegistryKey<ItemGroup> @NotNull ... groups) {
		var key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(this.namespace, path));
		var item = function.apply(settings.registryKey(key));
		Registry.register(Registries.ITEM, key, item);
		for (var group : groups) this.addToItemGroup(group, item.getDefaultStack());
		return item;
	}

	public void addToItemGroup(RegistryKey<ItemGroup> group, ItemStack stack) {
		ItemGroupEvents.modifyEntriesEvent(group).register((entries) -> entries.add(stack));
	}

	public <T extends Entity> EntityType<T> register(String path, EntityType.@NotNull Builder<T> builder) {
		var key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(this.namespace, path));
		var entity = builder.build(key);
		return this.register(path, entity);
	}

	public <T extends LivingEntity> EntityType<T> register(String path, EntityType.@NotNull Builder<T> builder, @Nullable DefaultAttributeContainer.Builder attributes) {
		var key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(this.namespace, path));
		var entity = builder.build(key);
		return this.register(path, entity, attributes);
	}

	public <T extends LivingEntity> EntityType<T> register(String path, EntityType<T> entity, @Nullable DefaultAttributeContainer.Builder attributes) {
		this.register(path, entity);
		if (attributes != null) FabricDefaultAttributeRegistry.register(entity, attributes);
		if (entity != null) this.register(path + "_spawn_egg", new Item.Settings(), (s) -> new SpawnEggItem(s.spawnEgg(entity)), ItemGroups.SPAWN_EGGS);
		return entity;
	}

	public SoundEvent registerSound(String name) {
		var id = Identifier.of(this.namespace, name);
		return this.register(id, SoundEvent.of(id));
	}

	public RegistryKey<SoundEvent> registerSoundKey(String name) {
		var id = Identifier.of(this.namespace, name);
		return this.registerKey(id, SoundEvent.of(id));
	}

	public <T> T register(String path, T thing) {
		return this.register(Identifier.of(this.namespace, path), thing);
	}

	public <T> RegistryKey<T> registerKey(String path, T thing) {
		return this.registerKey(Identifier.of(this.namespace, path), thing);
	}

	public @SuppressWarnings("unchecked") <T> T register(Identifier id, T thing) {
		var registered = false;
		for (var registry : this.registries.entrySet()) {
			if (!registry.getKey().isInstance(thing)) continue;
			Registry.register(((Registry<T>) registry.getValue()), id, thing);
			registered = true;
			break;
		}
		if (registered) return thing;
		var error = new IllegalStateException("Failed to register " + id + " to the " + this.namespace + " MRegistry!");
		Mialib.LOGGER.error("Failed to register {} to the {} MRegistry!", id, this.namespace, error);
		throw error;
	}

	public @SuppressWarnings("unchecked") <T> RegistryKey<T> registerKey(Identifier id, T thing) {
		for (var registry : this.registries.entrySet()) {
			if (!registry.getKey().isInstance(thing)) continue;
			var reg = (Registry<T>) registry.getValue();
			var key = RegistryKey.of(reg.getKey(), id);
			Registry.register(reg, key, thing);
			return key;
		}
		var error = new IllegalStateException("Failed to register " + id + " to the " + this.namespace + " MRegistry!");
		Mialib.LOGGER.error("Failed to register {} to the {} MRegistry!", id, this.namespace, error);
		throw error;
	}
	
	static {
		DEFAULT_REGISTRIES.put(GameEvent.class, Registries.GAME_EVENT);
		DEFAULT_REGISTRIES.put(SoundEvent.class, Registries.SOUND_EVENT);
		DEFAULT_REGISTRIES.put(Fluid.class, Registries.FLUID);
		DEFAULT_REGISTRIES.put(StatusEffect.class, Registries.STATUS_EFFECT);
		DEFAULT_REGISTRIES.put(Block.class, Registries.BLOCK);
		DEFAULT_REGISTRIES.put(EntityType.class, Registries.ENTITY_TYPE);
		DEFAULT_REGISTRIES.put(Item.class, Registries.ITEM);
		DEFAULT_REGISTRIES.put(Potion.class, Registries.POTION);
		DEFAULT_REGISTRIES.put(ParticleType.class, Registries.PARTICLE_TYPE);
		DEFAULT_REGISTRIES.put(BlockEntityType.class, Registries.BLOCK_ENTITY_TYPE);
		DEFAULT_REGISTRIES.put(Identifier.class, Registries.CUSTOM_STAT);
		DEFAULT_REGISTRIES.put(ChunkStatus.class, Registries.CHUNK_STATUS);
		DEFAULT_REGISTRIES.put(RuleTestType.class, Registries.RULE_TEST);
		DEFAULT_REGISTRIES.put(RuleBlockEntityModifierType.class, Registries.RULE_BLOCK_ENTITY_MODIFIER);
		DEFAULT_REGISTRIES.put(PosRuleTestType.class, Registries.POS_RULE_TEST);
		DEFAULT_REGISTRIES.put(ScreenHandlerType.class, Registries.SCREEN_HANDLER);
		DEFAULT_REGISTRIES.put(RecipeType.class, Registries.RECIPE_TYPE);
		DEFAULT_REGISTRIES.put(RecipeSerializer.class, Registries.RECIPE_SERIALIZER);
		DEFAULT_REGISTRIES.put(EntityAttribute.class, Registries.ATTRIBUTE);
		DEFAULT_REGISTRIES.put(PositionSourceType.class, Registries.POSITION_SOURCE_TYPE);
		DEFAULT_REGISTRIES.put(ArgumentSerializer.class, Registries.COMMAND_ARGUMENT_TYPE);
		DEFAULT_REGISTRIES.put(StatType.class, Registries.STAT_TYPE);
		DEFAULT_REGISTRIES.put(VillagerType.class, Registries.VILLAGER_TYPE);
		DEFAULT_REGISTRIES.put(VillagerProfession.class, Registries.VILLAGER_PROFESSION);
		DEFAULT_REGISTRIES.put(PointOfInterestType.class, Registries.POINT_OF_INTEREST_TYPE);
		DEFAULT_REGISTRIES.put(MemoryModuleType.class, Registries.MEMORY_MODULE_TYPE);
		DEFAULT_REGISTRIES.put(SensorType.class, Registries.SENSOR_TYPE);
		DEFAULT_REGISTRIES.put(Schedule.class, Registries.SCHEDULE);
		DEFAULT_REGISTRIES.put(Activity.class, Registries.ACTIVITY);
		DEFAULT_REGISTRIES.put(LootPoolEntryType.class, Registries.LOOT_POOL_ENTRY_TYPE);
		DEFAULT_REGISTRIES.put(LootFunctionType.class, Registries.LOOT_FUNCTION_TYPE);
		DEFAULT_REGISTRIES.put(LootConditionType.class, Registries.LOOT_CONDITION_TYPE);
		DEFAULT_REGISTRIES.put(LootNumberProviderType.class, Registries.LOOT_NUMBER_PROVIDER_TYPE);
		DEFAULT_REGISTRIES.put(LootNbtProviderType.class, Registries.LOOT_NBT_PROVIDER_TYPE);
		DEFAULT_REGISTRIES.put(LootScoreProviderType.class, Registries.LOOT_SCORE_PROVIDER_TYPE);
		DEFAULT_REGISTRIES.put(FloatProviderType.class, Registries.FLOAT_PROVIDER_TYPE);
		DEFAULT_REGISTRIES.put(IntProviderType.class, Registries.INT_PROVIDER_TYPE);
		DEFAULT_REGISTRIES.put(HeightProviderType.class, Registries.HEIGHT_PROVIDER_TYPE);
		DEFAULT_REGISTRIES.put(BlockPredicateType.class, Registries.BLOCK_PREDICATE_TYPE);
		DEFAULT_REGISTRIES.put(Carver.class, Registries.CARVER);
		DEFAULT_REGISTRIES.put(Feature.class, Registries.FEATURE);
		DEFAULT_REGISTRIES.put(StructurePlacementType.class, Registries.STRUCTURE_PLACEMENT);
		DEFAULT_REGISTRIES.put(StructurePieceType.class, Registries.STRUCTURE_PIECE);
		DEFAULT_REGISTRIES.put(StructureType.class, Registries.STRUCTURE_TYPE);
		DEFAULT_REGISTRIES.put(PlacementModifierType.class, Registries.PLACEMENT_MODIFIER_TYPE);
		DEFAULT_REGISTRIES.put(BlockStateProviderType.class, Registries.BLOCK_STATE_PROVIDER_TYPE);
		DEFAULT_REGISTRIES.put(FoliagePlacerType.class, Registries.FOLIAGE_PLACER_TYPE);
		DEFAULT_REGISTRIES.put(TrunkPlacerType.class, Registries.TRUNK_PLACER_TYPE);
		DEFAULT_REGISTRIES.put(RootPlacerType.class, Registries.ROOT_PLACER_TYPE);
		DEFAULT_REGISTRIES.put(TreeDecoratorType.class, Registries.TREE_DECORATOR_TYPE);
		DEFAULT_REGISTRIES.put(FeatureSizeType.class, Registries.FEATURE_SIZE_TYPE);
		DEFAULT_REGISTRIES.put(StructureProcessorType.class, Registries.STRUCTURE_PROCESSOR);
		DEFAULT_REGISTRIES.put(StructurePoolElementType.class, Registries.STRUCTURE_POOL_ELEMENT);
		DEFAULT_REGISTRIES.put(DecoratedPotPattern.class, Registries.DECORATED_POT_PATTERN);
		DEFAULT_REGISTRIES.put(ItemGroup.class, Registries.ITEM_GROUP);
		DEFAULT_REGISTRIES.put(Criterion.class, Registries.CRITERION);
		DEFAULT_REGISTRIES.put(NumberFormatType.class, Registries.NUMBER_FORMAT_TYPE);
		DEFAULT_REGISTRIES.put(ComponentType.class, Registries.DATA_COMPONENT_TYPE);
		DEFAULT_REGISTRIES.put(MapDecorationType.class, Registries.MAP_DECORATION_TYPE);
		DEFAULT_REGISTRIES.put(ConsumeEffect.Type.class, Registries.CONSUME_EFFECT_TYPE);
		DEFAULT_REGISTRIES.put(RecipeDisplay.Serializer.class, Registries.RECIPE_DISPLAY);
		DEFAULT_REGISTRIES.put(SlotDisplay.Serializer.class, Registries.SLOT_DISPLAY);
		DEFAULT_REGISTRIES.put(RecipeBookCategory.class, Registries.RECIPE_BOOK_CATEGORY);
	}
}