package xyz.amymialee.mialib.modules;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;
import xyz.amymialee.mialib.mvalues.MValue;

public interface ExtrasModule {
    TagKey<Item> UNDESTROYABLE = TagKey.of(Registries.ITEM.getKey(), Mialib.id("damage_immune"));
    TagKey<Item> UNBREAKABLE = TagKey.of(Registries.ITEM.getKey(), Mialib.id("unbreakable"));

    MValue<Boolean> DISABLE_PIGLIN_PORTAL_SPAWNING = MValue.of(Mialib.id("disable_piglin_portal_spawning"), MValue.BOOLEAN_FALSE).item((v) -> v.get() ? Items.ROTTEN_FLESH : Items.GOLD_NUGGET).build();
    MValue<Boolean> DISABLE_END_PORTALS = MValue.of(Mialib.id("disable_end_portals"), MValue.BOOLEAN_FALSE).item((v) -> v.get() ? Items.END_STONE_BRICK_SLAB : Items.END_PORTAL_FRAME).build();

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