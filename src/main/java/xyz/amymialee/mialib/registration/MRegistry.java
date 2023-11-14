package xyz.amymialee.mialib.registration;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSourceType;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialib.MiaLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MRegistry {
	private static final Map<Class<?>, Registry<?>> DEFAULT_REGISTRIES = new HashMap<>();
	public static final List<MRegistry> REGISTRIES = new ArrayList<>();
	private final String namespace;
	private final Map<Registry<?>, Map<Identifier, Object>> objects;
	private final List<Runnable> itemGroupRegistrations = new ArrayList<>();
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
			this.itemGroupRegistrations.add(() -> Registries.ITEM_GROUP.getKey(group).ifPresent(itemGroupRegistryKey -> ItemGroupEvents.modifyEntriesEvent(itemGroupRegistryKey).register(content -> content.add(item))));
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

	public Block registerBlock(String path, Block block, boolean registerItem) {
		this.register(path, block);
		if (registerItem) {
			this.register(path, new BlockItem(block, new FabricItemSettings()));
		}
		return block;
	}

	public <T extends MobEntity> EntityType<T> registerEntity(String path, EntityType<T> entity) {
		return this.registerEntity(path, entity, null, null);
	}

	public <T extends MobEntity> EntityType<T> registerEntity(String path, EntityType<T> entity, @Nullable EggData eggData) {
		return this.registerEntity(path, entity, null, eggData);
	}

	public <T extends MobEntity> EntityType<T> registerEntity(String path, EntityType<T> entity, @Nullable DefaultAttributeContainer attributes) {
		return this.registerEntity(path, entity, attributes, null);
	}

	@SuppressWarnings("DataFlowIssue")
	public <T extends MobEntity> EntityType<T> registerEntity(String path, EntityType<T> entity, @Nullable DefaultAttributeContainer attributes, @Nullable EggData eggData) {
		this.register(path, entity);
		if (attributes != null) {
			FabricDefaultAttributeRegistry.register(entity, attributes);
		}
		if (eggData != null) {
			var egg = new SpawnEggItem(entity, eggData.primaryColor, eggData.secondaryColor, new FabricItemSettings());
			this.register(path + "_spawn_egg", egg);
			this.spawnEggs.put(entity, egg);
		}
		return entity;
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

	@SuppressWarnings("unchecked")
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
        this.itemGroupRegistrations.clear();
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
	}

	public record EggData(int primaryColor, int secondaryColor) {
		public EggData(int color) {
			this(color, color);
		}
	}
}