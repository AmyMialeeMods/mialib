package xyz.amymialee.mialib;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;
import xyz.amymialee.mialib.cca.HoldingComponent;
import xyz.amymialee.mialib.cca.IdCooldownComponent;
import xyz.amymialee.mialib.detonations.Detonation;
import xyz.amymialee.mialib.events.MiaLibEvents;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueCategory;
import xyz.amymialee.mialib.mvalues.MValueManager;

import java.util.Optional;

public class MiaLib implements ModInitializer, EntityComponentInitializer, ScoreboardComponentInitializer {
    public static final String MOD_ID = "mialib";
    public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getName();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    // Components
    public static final ComponentKey<IdCooldownComponent> ID_COOLDOWN_COMPONENT = ComponentRegistry.getOrCreate(id("identifier_cooldown"), IdCooldownComponent.class);
    public static final ComponentKey<HoldingComponent> HOLDING = ComponentRegistry.getOrCreate(id("holding"), HoldingComponent.class);
    public static final ComponentKey<ExtraFlagsComponent> EXTRA_FLAGS = ComponentRegistry.getOrCreate(id("extra_flags"), ExtraFlagsComponent.class);
    // Tags
    public static final TagKey<Item> SOUL_FIRE_SMELTING = TagKey.of(Registries.ITEM.getKey(), id("soul_fire_smelting"));
    public static final TagKey<Item> UNDESTROYABLE = TagKey.of(Registries.ITEM.getKey(), id("damage_immune"));
    public static final TagKey<Item> UNCRAFTABLE = TagKey.of(Registries.ITEM.getKey(), id("uncraftable"));
    public static final TagKey<Item> UNBREAKABLE = TagKey.of(Registries.ITEM.getKey(), id("unbreakable"));
    // MValues
    public static final MValueCategory MIALIB_CATEGORY = new MValueCategory(id(MOD_ID), Items.DIAMOND.getDefaultStack(), new Identifier("textures/block/purple_concrete.png"));
    public static final MValue.MValueBoolean CREATIVE_NO_SLEEP = MValue.ofBoolean(MIALIB_CATEGORY, id("creative_no_sleep"), Items.RED_BED.getDefaultStack(), false);
    public static final MValue.MValueBoolean FIRE_ASPECT_AUTO_SMELT = MValue.ofBoolean(MIALIB_CATEGORY, id("fire_aspect_auto_smelt"), Items.FIRE_CHARGE.getDefaultStack(), false);
    public static final MValue.MValueBoolean DISABLE_PIGLIN_PORTAL_SPAWNING = MValue.ofBoolean(MIALIB_CATEGORY, id("disable_piglin_portal_spawning"), Items.GOLD_NUGGET.getDefaultStack(), false);
    public static final MValue.MValueBoolean DISABLE_NETHER_PORTALS = MValue.ofBoolean(MIALIB_CATEGORY, id("disable_nether_portals"), Items.OBSIDIAN.getDefaultStack(), false);
    public static final MValue.MValueBoolean DISABLE_END_PORTALS = MValue.ofBoolean(MIALIB_CATEGORY, id("disable_end_portals"), Items.END_PORTAL_FRAME.getDefaultStack(), false);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> {
            for (var detonation : new Pair[]{
                    new Pair<>("ghast_fireball", Detonation.GHAST_FIREBALL),
                    new Pair<>("wither_skull", Detonation.WITHER_SKULL),
                    new Pair<>("creeper", Detonation.CREEPER),
                    new Pair<>("tnt", Detonation.TNT),
                    new Pair<>("bed", Detonation.BED),
                    new Pair<>("respawn_anchor", Detonation.RESPAWN_ANCHOR),
                    new Pair<>("charged_creeper", Detonation.CHARGED_CREEPER),
                    new Pair<>("end_crystal", Detonation.END_CRYSTAL),
                    new Pair<>("wither_spawn", Detonation.WITHER_SPAWN)}) {
                dispatcher.register(CommandManager.literal("detonate").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal((String) detonation.getLeft()).then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                                .executes(context -> {
                                    var pos = Vec3ArgumentType.getVec3(context, "pos");
                                    var world = context.getSource().getWorld();
                                    ((Detonation) detonation.getRight()).executeDetonation(world, pos);
                                    return 1;
                                }))));
            }
        });
        MiaLibEvents.SMELT_BROKEN_BLOCK.register((world, state, pos, blockEntity, entity, stack) -> {
            if (stack.getItem().mialib$shouldSmelt(world, state, pos, blockEntity, entity, stack)) {
                return ActionResult.SUCCESS;
            }
            if (FIRE_ASPECT_AUTO_SMELT.getValue() && entity instanceof LivingEntity living && EnchantmentHelper.getFireAspect(living) > 0) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        MiaLibEvents.DAMAGE_PREVENTION.register((entity, source) -> {
            var component = EXTRA_FLAGS.get(entity);
            return component.isIndestructible() || (!(entity instanceof LivingEntity) && component.isImmortal());
        });
        MiaLibEvents.DAMAGE_INTERACTION.register((entity, source, amount) -> {
            var component = EXTRA_FLAGS.get(entity);
            if (component.isImmortal() && amount > entity.getHealth()) {
                return Optional.of(entity.getHealth() - 1.0F);
            }
            return Optional.of(amount);
        });
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.getMainHandStack().getItem().mialib$killEntity(world, livingEntity.getMainHandStack(), livingEntity, killedEntity);
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(MValue.MVALUE_SYNC, ((server, player, handler, buf, responseSender) -> {
            if (!player.hasPermissionLevel(4)) return;
            var id = buf.readIdentifier();
            var nbt = buf.readNbt();
            server.execute(() -> {
                var mValue = MValueManager.get(id);
                if (mValue != null) {
                    mValue.readNbt(nbt);
                    mValue.syncAll();
                    MValueManager.saveConfig();
                }
            });
        }));
    }

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerFor(PlayerEntity.class, ID_COOLDOWN_COMPONENT, IdCooldownComponent::new);
        registry.beginRegistration(PlayerEntity.class, HOLDING).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(HoldingComponent::new);
        registry.beginRegistration(Entity.class, EXTRA_FLAGS).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(ExtraFlagsComponent::new);
    }

    @Override
    public void registerScoreboardComponentFactories(@NotNull ScoreboardComponentFactoryRegistry registry) {}

    public static @NotNull Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}