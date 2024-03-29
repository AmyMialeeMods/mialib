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
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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

import java.util.Optional;

public class MiaLib implements ModInitializer, EntityComponentInitializer, ScoreboardComponentInitializer {
    public static final String MOD_ID = "mialib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    // Components
    public static final ComponentKey<IdCooldownComponent> ID_COOLDOWN_COMPONENT = ComponentRegistry.getOrCreate(id("identifier_cooldown"), IdCooldownComponent.class);
    public static final ComponentKey<HoldingComponent> HOLDING = ComponentRegistry.getOrCreate(id("holding"), HoldingComponent.class);
    public static final ComponentKey<ExtraFlagsComponent> EXTRA_FLAGS = ComponentRegistry.getOrCreate(id("extra_flags"), ExtraFlagsComponent.class);
    // Tags
    public static final TagKey<Item> SOUL_FIRE_SMELTING = TagKey.of(Registries.ITEM.getKey(), id("soul_fire_smelting"));
    public static final TagKey<Item> UNDESTROYABLE = TagKey.of(Registries.ITEM.getKey(), id("damage_immune"));
    public static final TagKey<Item> UNCRAFTABLE = TagKey.of(Registries.ITEM.getKey(), id("uncraftable"));
    public static final TagKey<Item> UNBREAKABLE = TagKey.of(Registries.ITEM.getKey(), id("unbreakable"));

    // MValue Ideas
    // Fire Aspect Auto Smelt
    // Piglin Nether Portal Spawning Toggle
    // Nether Portal Functionality Toggle
    // End Portal Functionality Toggle

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> {
            for (var detonation : new Pair[]{
                    new Pair<>("creeper", Detonation.CREEPER),
                    new Pair<>("tnt", Detonation.TNT),
                    new Pair<>("charged_creeper", Detonation.CHARGED_CREEPER),
                    new Pair<>("end_crystal", Detonation.END_CRYSTAL)}) {
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