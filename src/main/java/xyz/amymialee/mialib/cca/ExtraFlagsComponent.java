package xyz.amymialee.mialib.cca;

import com.mojang.brigadier.arguments.BoolArgumentType;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.MMath;
import xyz.amymialee.mialib.util.TriFunction;

/**
 * <p>
 * Stores an extra set of flags for entities.
 * </p>
 * <p>
 * Keeps flags stored as a byte, with each bit representing a different flag.
 * </p>
 * <p>
 * Also stores a set of command toggles for each flag.
 * </p>
 * <p>
 * Includes 3 flags:
 * </p>
 * <p>
 *  - Imperceptible: The entity is completely invisible, including feature renderers.
 * </p>
 * <p>
 *  - Indestructible: The entity cannot be damaged or destroyed.
 * </p>
 * <p>
 *  - Immortal: The entity cannot die, but can take damage down to 1hp.
 * </p>
 */
public class ExtraFlagsComponent implements AutoSyncedComponent {
    private final Entity entity;
    private byte flags = 0;
    private byte commandFlags = 0;
    private static final TriFunction<HaveFlagCallback[], World, Entity, ActionResult> CALLBACK_RESULT = (callbacks, world, entity) -> {
        var succeeded = false;
        for (var callback : callbacks) {
            var result = callback.shouldHaveFlag(world, entity);
            if (result.isAccepted()) {
                succeeded = true;
                continue;
            }
            if (result != ActionResult.PASS) {
                return result;
            }
        }
        if (succeeded) {
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    };
    public static final Event<HaveFlagCallback> SHOULD_BE_IMPERCEPTIBLE = EventFactory.createArrayBacked(HaveFlagCallback.class, callbacks -> (world, entity) -> CALLBACK_RESULT.apply(callbacks, world, entity));
    public static final Event<HaveFlagCallback> SHOULD_BE_INDESTRUCTIBLE = EventFactory.createArrayBacked(HaveFlagCallback.class, callbacks -> (world, entity) -> CALLBACK_RESULT.apply(callbacks, world, entity));
    public static final Event<HaveFlagCallback> SHOULD_BE_IMMORTAL = EventFactory.createArrayBacked(HaveFlagCallback.class, callbacks -> (world, entity) -> CALLBACK_RESULT.apply(callbacks, world, entity));

    public ExtraFlagsComponent(Entity entity) {
        this.entity = entity;
    }

    public void sync() {
        MiaLib.EXTRA_FLAGS.sync(this.entity);
    }

    public boolean isImperceptible() {
        return MMath.getByteFlag(this.flags, 0);
    }

    private void setImperceptible(boolean imperceptible) {
        if (imperceptible == this.isImperceptible()) return;
        this.flags = MMath.setByteFlag(this.flags, 0, imperceptible);
        this.sync();
    }

    public boolean hasImperceptibleCommand() {
        return MMath.getByteFlag(this.commandFlags, 0);
    }

    public void setImperceptibleCommand(boolean imperceptible) {
        if (imperceptible == this.hasImperceptibleCommand()) return;
        this.commandFlags = MMath.setByteFlag(this.commandFlags, 0, imperceptible);
        this.sync();
        this.refreshFlags();
    }

    public boolean isIndestructible() {
        return MMath.getByteFlag(this.flags, 1);
    }

    private void setIndestructible(boolean indestructible) {
        if (indestructible == this.isIndestructible()) return;
        this.flags = MMath.setByteFlag(this.flags, 1, indestructible);
        this.sync();
    }

    public boolean hasIndestructibleCommand() {
        return MMath.getByteFlag(this.commandFlags, 1);
    }

    public void setIndestructibleCommand(boolean indestructible) {
        if (indestructible == this.hasIndestructibleCommand()) return;
        this.commandFlags = MMath.setByteFlag(this.commandFlags, 1, indestructible);
        this.sync();
        this.refreshFlags();
    }

    public boolean isImmortal() {
        return MMath.getByteFlag(this.flags, 2);
    }

    private void setImmortal(boolean immortal) {
        if (immortal == this.isImmortal()) return;
        this.flags = MMath.setByteFlag(this.flags, 2, immortal);
        this.sync();
    }

    public boolean hasImmortalCommand() {
        return MMath.getByteFlag(this.commandFlags, 2);
    }

    public void setImmortalCommand(boolean immortal) {
        if (immortal == this.hasImmortalCommand()) return;
        this.commandFlags = MMath.setByteFlag(this.commandFlags, 2, immortal);
        this.sync();
        this.refreshFlags();
    }

    public void refreshFlags() {
        this.setImperceptible(SHOULD_BE_IMPERCEPTIBLE.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
        this.setIndestructible(SHOULD_BE_INDESTRUCTIBLE.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
        this.setImmortal(SHOULD_BE_IMMORTAL.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        this.flags = tag.getByte("flags");
        this.commandFlags = tag.getByte("commandFlags");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        tag.putByte("flags", this.flags);
        tag.putByte("commandFlags", this.commandFlags);
    }

    static {
        SHOULD_BE_IMPERCEPTIBLE.register((world, entity) -> {
            var component = MiaLib.EXTRA_FLAGS.get(entity);
            if (component.hasImperceptibleCommand()) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        SHOULD_BE_INDESTRUCTIBLE.register((world, entity) -> {
            var component = MiaLib.EXTRA_FLAGS.get(entity);
            if (component.hasIndestructibleCommand()) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        SHOULD_BE_IMMORTAL.register((world, entity) -> {
            var component = MiaLib.EXTRA_FLAGS.get(entity);
            if (component.hasImmortalCommand()) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> dispatcher.register(
                CommandManager.literal("vanish").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                        .executes(ctx -> {
                                            var enabled = BoolArgumentType.getBool(ctx, "enabled");
                                            var targets = EntityArgumentType.getEntities(ctx, "targets");
                                            for (var target : targets) {
                                                var component = MiaLib.EXTRA_FLAGS.get(target);
                                                component.setImperceptibleCommand(enabled);
                                            }
                                            ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.vanish." + (enabled ? "enabled" : "disabled") + (targets.size() == 1 ? ".single" : ".multiple"), targets.size() == 1 ? targets.iterator().next().getDisplayName() : targets.size()), true);
                                            return targets.size();
                                        })
                                ).executes(ctx -> {
                                    var enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    var user = ctx.getSource().getPlayer();
                                    if (user != null) {
                                        var component = MiaLib.EXTRA_FLAGS.get(user);
                                        component.setImperceptibleCommand(enabled);
                                        ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.vanish." + (enabled ? "enabled" : "disabled") + ".self", user.getDisplayName()), true);
                                        return 1;
                                    }
                                    return 0;
                                })
                        ).executes(ctx -> {
                            var user = ctx.getSource().getPlayer();
                            if (user != null) {
                                var component = MiaLib.EXTRA_FLAGS.get(user);
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
                                                var component = MiaLib.EXTRA_FLAGS.get(target);
                                                component.setIndestructibleCommand(enabled);
                                            }
                                            ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.indestructible." + (enabled ? "enabled" : "disabled") + (targets.size() == 1 ? ".single" : ".multiple"), targets.size() == 1 ? targets.iterator().next().getDisplayName() : targets.size()), true);
                                            return targets.size();
                                        })
                                ).executes(ctx -> {
                                    var enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    var user = ctx.getSource().getPlayer();
                                    if (user != null) {
                                        var component = MiaLib.EXTRA_FLAGS.get(user);
                                        component.setIndestructibleCommand(enabled);
                                        ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.indestructible." + (enabled ? "enabled" : "disabled") + ".self", user.getDisplayName()), true);
                                        return 1;
                                    }
                                    return 0;
                                })
                        ).executes(ctx -> {
                            var user = ctx.getSource().getPlayer();
                            if (user != null) {
                                var component = MiaLib.EXTRA_FLAGS.get(user);
                                var enabled = !component.hasIndestructibleCommand();
                                component.setIndestructibleCommand(enabled);
                                ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.indestructible." + (enabled ? "enabled" : "disabled") + ".self", user.getDisplayName()), true);
                                return 1;
                            }
                            return 0;
                        })
        ));
        CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> dispatcher.register(
                CommandManager.literal("immortal").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                        .executes(ctx -> {
                                            var enabled = BoolArgumentType.getBool(ctx, "enabled");
                                            var targets = EntityArgumentType.getEntities(ctx, "targets");
                                            for (var target : targets) {
                                                var component = MiaLib.EXTRA_FLAGS.get(target);
                                                component.setImmortalCommand(enabled);
                                            }
                                            ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.immortal." + (enabled ? "enabled" : "disabled") + (targets.size() == 1 ? ".single" : ".multiple"), targets.size() == 1 ? targets.iterator().next().getDisplayName() : targets.size()), true);
                                            return targets.size();
                                        })
                                ).executes(ctx -> {
                                    var enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    var user = ctx.getSource().getPlayer();
                                    if (user != null) {
                                        var component = MiaLib.EXTRA_FLAGS.get(user);
                                        component.setImmortalCommand(enabled);
                                        ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.immortal." + (enabled ? "enabled" : "disabled") + ".self", user.getDisplayName()), true);
                                        return 1;
                                    }
                                    return 0;
                                })
                        ).executes(ctx -> {
                            var user = ctx.getSource().getPlayer();
                            if (user != null) {
                                var component = MiaLib.EXTRA_FLAGS.get(user);
                                var enabled = !component.hasImmortalCommand();
                                component.setImmortalCommand(enabled);
                                ctx.getSource().sendFeedback(() -> Text.translatable("commands.mialib.immortal." + (enabled ? "enabled" : "disabled") + ".self", user.getDisplayName()), true);
                                return 1;
                            }
                            return 0;
                        })
        ));
    }

    @FunctionalInterface
    public interface HaveFlagCallback {
        ActionResult shouldHaveFlag(World world, Entity entity);
    }
}