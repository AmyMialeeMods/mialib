package xyz.amymialee.mialib.modules;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;

public interface CommandModule {
    static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> {
            dispatcher.register(CommandManager.literal("indestructible").requires(source -> source.hasPermissionLevel(4))
                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                            .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                    .executes(ctx -> executeIndestructible(ctx.getSource(), BoolArgumentType.getBool(ctx, "enabled"), EntityArgumentType.getEntities(ctx, "targets").toArray(new Entity[0])))
                            ).executes(ctx -> executeIndestructible(ctx.getSource(), BoolArgumentType.getBool(ctx, "enabled"), ctx.getSource().getPlayer()))
                    ).executes(ctx -> ctx.getSource().getPlayer() == null ? 0 : executeIndestructible(ctx.getSource(), !ExtraFlagsComponent.KEY.get(ctx.getSource().getPlayer()).hasIndestructibleCommand(), ctx.getSource().getPlayer()))
            );
            dispatcher.register(CommandManager.literal("immortal").requires(source -> source.hasPermissionLevel(4))
                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                            .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                    .executes(ctx -> executeImmortal(ctx.getSource(), BoolArgumentType.getBool(ctx, "enabled"), EntityArgumentType.getEntities(ctx, "targets").toArray(new Entity[0])))
                            ).executes(ctx -> executeImmortal(ctx.getSource(), BoolArgumentType.getBool(ctx, "enabled"), ctx.getSource().getPlayer()))
                    ).executes(ctx -> ctx.getSource().getPlayer() == null ? 0 : executeImmortal(ctx.getSource(), !ExtraFlagsComponent.KEY.get(ctx.getSource().getPlayer()).hasImmortalCommand(), ctx.getSource().getPlayer()))
            );
            dispatcher.register(CommandManager.literal("fly").requires(source -> source.hasPermissionLevel(4))
                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                            .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                    .executes(ctx -> executeFly(ctx.getSource(), BoolArgumentType.getBool(ctx, "enabled"), EntityArgumentType.getEntities(ctx, "targets").toArray(new Entity[0])))
                            ).executes(ctx -> executeFly(ctx.getSource(), BoolArgumentType.getBool(ctx, "enabled"), ctx.getSource().getPlayer()))
                    ).executes(ctx -> ctx.getSource().getPlayer() == null ? 0 : executeFly(ctx.getSource(), !ExtraFlagsComponent.KEY.get(ctx.getSource().getPlayer()).hasFlyCommand(), ctx.getSource().getPlayer()))
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

    @SafeVarargs
    static <T extends Entity> int executeFly(ServerCommandSource source, boolean immortal, T @NotNull ... targets) {
        for (var target : targets) ExtraFlagsComponent.KEY.maybeGet(target).ifPresent(extraFlagsComponent -> extraFlagsComponent.setFlyCommand(immortal));
        source.sendFeedback(() -> Text.translatable("commands.mialib.fly.%s.%s".formatted(immortal ? "enabled" : "disabled", targets.length == 1 ? "single" : "multiple"), targets.length == 1 ? targets[0] != null ? targets[0].getDisplayName() : "Nobody" : targets.length), true);
        return targets.length;
    }
}