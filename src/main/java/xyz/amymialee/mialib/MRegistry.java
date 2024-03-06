package xyz.amymialee.mialib;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MRegistry {
	private static final Map<Class<?>, Registry<?>> DEFAULT_REGISTRIES = new HashMap<>();
	public static final List<MRegistry> REGISTRIES = new ArrayList<>();
	private static boolean builtAll = false;
	private final String namespace;
	private final Map<Registry<?>, Map<Identifier, Object>> objects;
	private final List<Runnable> itemGroupRegistrations = new ArrayList<>();
	private final List<Runnable> entityAttributeRegistrations = new ArrayList<>();
	public final Map<EntityType<?>, SpawnEggItem> spawnEggs = new HashMap<>();
	private Map<Class<?>, Registry<?>> registries;
	private boolean built = false;

	public MRegistry(String namespace) {
		this.namespace = namespace;
		this.registries = new HashMap<>();
		this.registries.putAll(DEFAULT_REGISTRIES);
		this.objects = new HashMap<>();
		REGISTRIES.add(this);
	}

	public void clearRegistries() {
		this.registries = new HashMap<>();
	}

	public void addRegistry(Class<?> clazz, Registry<?> registry) {
		this.registries.put(clazz, registry);
	}

	public Item registerItem(String path, Item item, ItemGroup @NotNull ... groups) {
		for (var group : groups) {
			this.addToItemGroup(group, content -> content.add(item));
		}
		return this.register(path, item);
	}

	@SafeVarargs
	public final Item registerItem(String path, Item item, Consumer<Item> @NotNull ... groups) {
		for (var group : groups) {
			this.itemGroupRegistrations.add(() -> group.accept(item));
		}
		return this.register(path, item);
	}

	public void addToItemGroup(ItemGroup group, Consumer<FabricItemGroupEntries> consumer) {
		this.itemGroupRegistrations.add(() -> Registries.ITEM_GROUP.getKey(group).ifPresent(itemGroupRegistryKey -> ItemGroupEvents.modifyEntriesEvent(itemGroupRegistryKey).register(consumer::accept)));
	}

	public Block registerBlockWithItem(String path, Block block, ItemGroup @NotNull ... groups) {
		this.register(path, block);
		this.registerItem(path, new BlockItem(block, new FabricItemSettings()), groups);
		return block;
	}

	@SafeVarargs
	public final Block registerBlockWithItem(String path, Block block, Consumer<Item> @NotNull ... groups) {
		this.register(path, block);
		this.registerItem(path, new BlockItem(block, new FabricItemSettings()), groups);
		return block;
	}

	public <T extends MobEntity> EntityType<T> registerEntity(String path, EntityType<T> entity, @Nullable EggData eggData) {
		return this.registerEntity(path, entity, null, eggData);
	}

	public <T extends LivingEntity> EntityType<T> registerEntity(String path, EntityType<T> entity, @Nullable DefaultAttributeContainer.Builder attributes) {
		this.register(path, entity);
		if (attributes != null) {
			this.entityAttributeRegistrations.add(() -> FabricDefaultAttributeRegistry.register(entity, attributes));
		}
		return entity;
	}

	public <T extends MobEntity> EntityType<T> registerEntity(String path, EntityType<T> entity, @Nullable DefaultAttributeContainer.Builder attributes, @Nullable EggData eggData) {
		this.register(path, entity);
		if (attributes != null) {
			this.entityAttributeRegistrations.add(() -> FabricDefaultAttributeRegistry.register(entity, attributes));
		}
		if (eggData != null) {
			var egg = new SpawnEggItem(entity, eggData.primaryColor, eggData.secondaryColor, new FabricItemSettings());
			this.register(path + "_spawn_egg", egg);
			this.spawnEggs.put(entity, egg);
		}
		return entity;
	}

	public SoundEvent registerSound(String name) {
		var id = new Identifier(this.namespace, name);
		var event = SoundEvent.of(id);
		this.register(id, event);
		return event;
	}

	public <T> T register(String path, T thing) {
		return this.register(new Identifier(this.namespace, path), thing);
	}

	public <T> T register(Identifier id, T thing) {
		if (this.built) {
			var error = new IllegalStateException("Tried to register " + id + " to the " + this.namespace + " MRegistry after it was built!");
			MiaLib.LOGGER.error("Failed to register " + id + " to the " + this.namespace + " MRegistry after it was built!", error);
			throw error;
		}
		var registered = false;
		for (var registry : this.registries.entrySet()) {
			if (registry.getKey().isInstance(thing)) {
				this.objects.putIfAbsent(registry.getValue(), new HashMap<>());
				var map = this.objects.get(registry.getValue());
				if (map.containsKey(id)) {
					throw new IllegalStateException("Failed to register " + id + " as it already exists in the " + registry.getValue() + " " + this.namespace + " MRegistry.");
				}
				map.put(id, thing);
				registered = true;
				break;
			}
		}
		if (!registered) {
			var error = new IllegalStateException("Failed to register " + id + " to the " + this.namespace + " MRegistry!");
			MiaLib.LOGGER.error("Failed to register " + id + " to the " + this.namespace + " MRegistry!", error);
			throw error;
		}
		return thing;
	}

	public <T> void build() {
		if (this.built) {
			MiaLib.LOGGER.warn("Tried to build the " + this.namespace + " MRegistry twice!");
			return;
		}
		this.built = true;
		for (var regEntry : this.objects.entrySet()) {
			var registry = regEntry.getKey();
			for (var entry : regEntry.getValue().entrySet()) {
				Registry.register(((Registry<T>) registry), entry.getKey(), (T) entry.getValue());
			}
		}
		for (var itemGroupRegistration : this.itemGroupRegistrations) {
			itemGroupRegistration.run();
		}
		for (var entityAttributeRegistration : this.entityAttributeRegistrations) {
			entityAttributeRegistration.run();
		}
		this.itemGroupRegistrations.clear();
	}

	public static void tryBuildAll(String location) {
		if (!REGISTRIES.isEmpty()) {
			if (builtAll) {
				MiaLib.LOGGER.info("Tried to build all MiaLib Registries on %s, but it was already built.".formatted(location));
				return;
			}
			builtAll = true;
			MiaLib.LOGGER.info("Building %d MiaLib Registr%s on %s".formatted(REGISTRIES.size(), REGISTRIES.size() == 1 ? "y" : "ies", location));
			REGISTRIES.forEach(MRegistry::build);
		}
	}

	static {
		DEFAULT_REGISTRIES.put(GameEvent.class, Registries.GAME_EVENT);
		DEFAULT_REGISTRIES.put(SoundEvent.class, Registries.SOUND_EVENT);
		DEFAULT_REGISTRIES.put(Fluid.class, Registries.FLUID);
		DEFAULT_REGISTRIES.put(StatusEffect.class, Registries.STATUS_EFFECT);
		DEFAULT_REGISTRIES.put(Block.class, Registries.BLOCK);
		DEFAULT_REGISTRIES.put(Enchantment.class, Registries.ENCHANTMENT);
		DEFAULT_REGISTRIES.put(EntityType.class, Registries.ENTITY_TYPE);
		DEFAULT_REGISTRIES.put(Item.class, Registries.ITEM);
		DEFAULT_REGISTRIES.put(Potion.class, Registries.POTION);
		DEFAULT_REGISTRIES.put(ParticleType.class, Registries.PARTICLE_TYPE);
		DEFAULT_REGISTRIES.put(BlockEntityType.class, Registries.BLOCK_ENTITY_TYPE);
		DEFAULT_REGISTRIES.put(PaintingVariant.class, Registries.PAINTING_VARIANT);
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
		DEFAULT_REGISTRIES.put(CatVariant.class, Registries.CAT_VARIANT);
		DEFAULT_REGISTRIES.put(FrogVariant.class, Registries.FROG_VARIANT);
		DEFAULT_REGISTRIES.put(BannerPattern.class, Registries.BANNER_PATTERN);
		DEFAULT_REGISTRIES.put(Instrument.class, Registries.INSTRUMENT);
		DEFAULT_REGISTRIES.put(ItemGroup.class, Registries.ITEM_GROUP);
	}

	public record EggData(int primaryColor, int secondaryColor) {
		public EggData(int color) {
			this(color, color);
		}
	}
}