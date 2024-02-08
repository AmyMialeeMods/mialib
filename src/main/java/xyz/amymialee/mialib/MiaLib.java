package xyz.amymialee.mialib;

import com.mojang.brigadier.arguments.BoolArgumentType;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
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

import java.util.Objects;

public class MiaLib implements ModInitializer, EntityComponentInitializer {
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
    // Superflat Presets
    public static final RegistryKey<FlatLevelGeneratorPreset> DEV_READY = FlatLevelGeneratorPresets.of(id("dev_ready").toString());
    public static final RegistryKey<FlatLevelGeneratorPreset> BLAST_PROOF = FlatLevelGeneratorPresets.of(id("blast_proof").toString());

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> dispatcher.register(
                CommandManager.literal("vanish").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                        .executes(ctx -> {
                                            var enabled = BoolArgumentType.getBool(ctx, "enabled");
                                            var targets = EntityArgumentType.getEntities(ctx, "targets");
                                            for (var target : targets) {
                                                var component = EXTRA_FLAGS.get(target);
                                                component.setImperceptibleCommand(enabled);
                                            }
                                            ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.vanish." + (enabled ? "enabled" : "disabled") + (targets.size() == 1 ? "single" : "multiple"), targets.size() == 1 ? targets.iterator().next().getDisplayName() : targets.size()), true);
                                            return targets.size();
                                        })
                                ).executes(ctx -> {
                                    var enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    var user = ctx.getSource().getPlayer();
                                    if (user != null) {
                                        var component = EXTRA_FLAGS.get(user);
                                        component.setImperceptibleCommand(enabled);
                                        ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.vanish." + (enabled ? "enabled" : "disabled") + ".self", user.getDisplayName()), true);
                                        return 1;
                                    }
                                    return 0;
                                })
                        ).executes(ctx -> {
                            var user = ctx.getSource().getPlayer();
                            if (user != null) {
                                var component = EXTRA_FLAGS.get(user);
                                var enabled = !component.hasImperceptibleCommand();
                                component.setImperceptibleCommand(enabled);
                                ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.vanish." + (enabled ? "enabled" : "disabled") + ".self", user.getDisplayName()), true);
                                return 1;
                            }
                            return 0;
                        })
        ));
        CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> dispatcher.register(
                CommandManager.literal("indestructible").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                        .executes(ctx -> {
                                            var enabled = BoolArgumentType.getBool(ctx, "enabled");
                                            var targets = EntityArgumentType.getEntities(ctx, "targets");
                                            for (var target : targets) {
                                                var component = EXTRA_FLAGS.get(target);
                                                component.setIndestructibleCommand(enabled);
                                            }
                                            ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.indestructible." + (enabled ? "enabled" : "disabled") + (targets.size() == 1 ? "single" : "multiple"), targets.size() == 1 ? targets.iterator().next().getDisplayName() : targets.size()), true);
                                            return targets.size();
                                        })
                                ).executes(ctx -> {
                                    var enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    var user = ctx.getSource().getPlayer();
                                    if (user != null) {
                                        var component = EXTRA_FLAGS.get(user);
                                        component.setIndestructibleCommand(enabled);
                                        ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.indestructible." + (enabled ? "enabled" : "disabled") + ".self", user.getDisplayName()), true);
                                        return 1;
                                    }
                                    return 0;
                                })
                        ).executes(ctx -> {
                            var user = ctx.getSource().getPlayer();
                            if (user != null) {
                                var component = EXTRA_FLAGS.get(user);
                                var enabled = !component.hasIndestructibleCommand();
                                component.setIndestructibleCommand(enabled);
                                ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.indestructible." + (enabled ? "enabled" : "disabled") + ".self", user.getDisplayName()), true);
                                return 1;
                            }
                            return 0;
                        })
        ));
        ServerPlayNetworking.registerGlobalReceiver(id("gamerule"), ((server, player, handler, buf, responseSender) -> {
            if (server.getPermissionLevel(player.getGameProfile()) >= 2) {
                var name = buf.readString();
                var category = buf.readString();
                var value = buf.readString();
                server.execute(() -> new GameRules.Visitor() {
                    @Override
                    public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                        if (Objects.equals(key.getName(), name) && Objects.equals(key.getCategory().getCategory(), category)) {
                            server.getGameRules().get(key).deserialize(value);
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
        ServerPlayNetworking.registerGlobalReceiver(id("attacking"), (server, player, handler, buf, responseSender) -> {
            var holding = buf.readBoolean();
            server.execute(() -> player.miaLib$setHoldingAttack(holding));
        });
        ServerPlayNetworking.registerGlobalReceiver(id("using"), (server, player, handler, buf, responseSender) -> {
            var holding = buf.readBoolean();
            server.execute(() -> player.miaLib$setHoldingUse(holding));
        });
        MiaLibEvents.SMELT_BROKEN_BLOCK.register((world, state, pos, blockEntity, entity, stack) -> {
            if (stack.getItem().mialib$shouldSmelt(world, state, pos, blockEntity, entity, stack)) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        MiaLibEvents.DAMAGE_PREVENTION.register((entity, source) -> {
            var component = EXTRA_FLAGS.get(entity);
            return component.isIndestructible();
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

    public static @NotNull Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}