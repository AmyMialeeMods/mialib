package xyz.amymialee.mialib.modules;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;
import xyz.amymialee.mialib.detonations.Detonation;

public interface CommandModule {
    static void init() {
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
            dispatcher.register(CommandManager.literal("indestructible").requires(source -> source.hasPermissionLevel(4))
                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                            .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                    .executes(ctx -> executeIndestructible(ctx.getSource(), BoolArgumentType.getBool(ctx, "enabled"), EntityArgumentType.getEntities(ctx, "targets").toArray(new Entity[0])))
                            ).executes(ctx -> executeIndestructible(ctx.getSource(), BoolArgumentType.getBool(ctx, "enabled"), ctx.getSource().getPlayer()))
                    ).executes(ctx -> ctx.getSource().getPlayer() == null ? 0 : executeIndestructible(ctx.getSource(), !ctx.getSource().getPlayer().mialib$isIndestructible(), ctx.getSource().getPlayer()))
            );
            dispatcher.register(CommandManager.literal("immortal").requires(source -> source.hasPermissionLevel(4))
                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                            .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                    .executes(ctx -> executeImmortal(ctx.getSource(), BoolArgumentType.getBool(ctx, "enabled"), EntityArgumentType.getEntities(ctx, "targets").toArray(new Entity[0])))
                            ).executes(ctx -> executeImmortal(ctx.getSource(), BoolArgumentType.getBool(ctx, "enabled"), ctx.getSource().getPlayer()))
                    ).executes(ctx -> ctx.getSource().getPlayer() == null ? 0 : executeImmortal(ctx.getSource(), !ctx.getSource().getPlayer().mialib$isImmortal(), ctx.getSource().getPlayer()))
            );
        });
    }

    @SafeVarargs
    static <T extends Entity> int executeIndestructible(ServerCommandSource source, boolean indestructible, T @NotNull ... targets) {
        for (var target : targets) ExtraFlagsComponent.KEY.maybeGet(target).ifPresent(extraFlagsComponent -> extraFlagsComponent.setIndestructibleCommand(indestructible));
        source.sendFeedback(() -> Text.translatable("commands.mialib.indestructible.%s.%s".formatted(indestructible ? "enabled" : "disabled", targets.length == 1 ? "single" : "multiple"), targets.length == 1 ? targets[0] != null ? targets[0].getDisplayName() : "Nobody" : targets.length), true);
        return targets.length;
    }

    @SafeVarargs
    static <T extends Entity> int executeImmortal(ServerCommandSource source, boolean immortal, T @NotNull ... targets) {
        for (var target : targets) ExtraFlagsComponent.KEY.maybeGet(target).ifPresent(extraFlagsComponent -> extraFlagsComponent.setImmortalCommand(immortal));
        source.sendFeedback(() -> Text.translatable("commands.mialib.immortal.%s.%s".formatted(immortal ? "enabled" : "disabled", targets.length == 1 ? "single" : "multiple"), targets.length == 1 ? targets[0] != null ? targets[0].getDisplayName() : "Nobody" : targets.length), true);
        return targets.length;
    }
}