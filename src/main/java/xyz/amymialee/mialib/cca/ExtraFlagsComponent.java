package xyz.amymialee.mialib.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.MMath;
import xyz.amymialee.mialib.util.TriFunction;

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

    public void refreshFlags() {
        this.setImperceptible(SHOULD_BE_IMPERCEPTIBLE.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
        this.setIndestructible(SHOULD_BE_INDESTRUCTIBLE.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
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
    }

    @FunctionalInterface
    public interface HaveFlagCallback {
        ActionResult shouldHaveFlag(World world, Entity entity);
    }
}