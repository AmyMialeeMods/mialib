package xyz.amymialee.mialib;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;
import xyz.amymialee.mialib.cca.HoldingComponent;
import xyz.amymialee.mialib.cca.IdCooldownComponent;
import xyz.amymialee.mialib.values.MValue;
import xyz.amymialee.mialib.values.MValueManager;
import xyz.amymialee.mialib.values.MValueType;

import java.util.Objects;

public class MiaLib implements ModInitializer, EntityComponentInitializer, ScoreboardComponentInitializer {
    public static final String MOD_ID = "mialib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    // Components
    public static final ComponentKey<IdCooldownComponent> ID_COOLDOWN_COMPONENT = ComponentRegistry.getOrCreate(id("identifier_cooldown"), IdCooldownComponent.class);
    public static final ComponentKey<HoldingComponent> HOLDING = ComponentRegistry.getOrCreate(id("holding"), HoldingComponent.class);
    public static final ComponentKey<ExtraFlagsComponent> FLAGS = ComponentRegistry.getOrCreate(id("extra_flags"), ExtraFlagsComponent.class);
    // Scoreboard Components
    public static final ComponentKey<MValueManager> MVALUE_MANAGER = ComponentRegistry.getOrCreate(id("mvalue_manager"), MValueManager.class);
    // Tags
    public static final TagKey<Item> SOUL_FIRE_SMELTING = TagKey.of(Registries.ITEM.getKey(), id("soul_fire_smelting"));
    public static final TagKey<Item> UNDESTROYABLE = TagKey.of(Registries.ITEM.getKey(), id("damage_immune"));
    public static final TagKey<Item> UNCRAFTABLE = TagKey.of(Registries.ITEM.getKey(), id("uncraftable"));
    public static final TagKey<Item> UNBREAKABLE = TagKey.of(Registries.ITEM.getKey(), id("unbreakable"));
    // Superflat Presets
    public static final RegistryKey<FlatLevelGeneratorPreset> DEV_READY = FlatLevelGeneratorPresets.of("dev_ready");
    public static final RegistryKey<FlatLevelGeneratorPreset> BLAST_PROOF = FlatLevelGeneratorPresets.of("blast_proof");

    public static final MValue<Boolean> FIRE_ASPECT_AUTOSMELTING = new MValue<>(id("fire_aspect_autosmelting"), (b) -> b ? Items.TORCH.getDefaultStack() : Items.LEVER.getDefaultStack(), MValueType.BOOLEAN, false);
//    public static final MIntegerValue TEST_INTEGER = new MIntegerValue(id("test_integer"), (i) -> i > 50 ? Items.COOKED_SALMON.getDefaultStack() : Items.SALMON.getDefaultStack(), 0, 0, 100);
//    public static final MLongValue TEST_LONG = new MLongValue(id("test_long"), (l) -> l > 50 ? Items.COOKED_SALMON.getDefaultStack() : Items.SALMON.getDefaultStack(), 0L, 0L, 100L);
//    public static final MFloatValue TEST_FLOAT = new MFloatValue(id("test_float"), (f) -> f > 50 ? Items.COOKED_SALMON.getDefaultStack() : Items.SALMON.getDefaultStack(), 0.0F, 0.0F, 100.0F);
//    public static final MDoubleValue TEST_DOUBLE = new MDoubleValue(id("test_double"), (d) -> d > 50 ? Items.COOKED_SALMON.getDefaultStack() : Items.SALMON.getDefaultStack(), 0.0D, 0.0D, 100.0D);

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(id("gamerule"), ((server, player, handler, buf, responseSender) -> {
            if (server.getPermissionLevel(player.getGameProfile()) >= 2) {
                var name = buf.readString();
                var category = buf.readString();
                var value = buf.readString();
                server.execute(() -> new GameRules.Visitor() {
                    @Override
                    public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                        if (Objects.equals(key.getName(), name) && Objects.equals(key.getCategory().getCategory(), category)) {
                            var rule = server.getGameRules().get(key);
                            rule.deserialize(value);
                        }
                    }
                });
            }
        }));
        ServerPlayNetworking.registerGlobalReceiver(MiaLib.id("mvaluesync"), (server, player, handler, buf, responseSender) -> {
            var nbt = buf.readNbt();
            if (nbt == null) return;
            server.execute(() -> {
                if (player.hasPermissionLevel(2)) {
                    var value = MValueManager.getValues().get(new Identifier(nbt.getString("id")));
                    if (value != null) {
                        value.readFromNbt(nbt);
                    }
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(id("holding"), (server, player, handler, buf, responseSender) -> {
            var holding = buf.readBoolean();
            server.execute(() -> player.miaLib$setHoldingAttack(holding));
        });
        MiaLibEvents.SMELT_BROKEN_BLOCK.register((world, state, pos, blockEntity, entity, stack) -> {
            if (stack.getItem().mialib$shouldSmelt(world, state, pos, blockEntity, entity, stack)) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        MiaLibEvents.SMELT_BROKEN_BLOCK.register((world, state, pos, blockEntity, entity, stack) -> {
            if (FIRE_ASPECT_AUTOSMELTING.getValue() && entity instanceof LivingEntity living && EnchantmentHelper.getFireAspect(living) > 0) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.getMainHandStack().getItem().mialib$killEntity(world, livingEntity.getMainHandStack(), livingEntity, killedEntity);
            }
        });
    }

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerFor(PlayerEntity.class, ID_COOLDOWN_COMPONENT, IdCooldownComponent::new);
        registry.beginRegistration(PlayerEntity.class, HOLDING).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(HoldingComponent::new);
        registry.beginRegistration(Entity.class, FLAGS).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(ExtraFlagsComponent::new);
    }

    @Override
    public void registerScoreboardComponentFactories(@NotNull ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(MVALUE_MANAGER, MValueManager::new);
    }

    public static @NotNull Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}