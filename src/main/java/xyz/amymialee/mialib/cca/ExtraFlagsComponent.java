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

import java.util.function.BiFunction;

public class ExtraFlagsComponent implements AutoSyncedComponent {
    private final Entity entity;
    private byte flags = 0;
    private static final TriFunction<HaveFlagCallback[], World, Entity, ActionResult> CALLBACK_RESULT;
    Event<HaveFlagCallback> SHOULD_BE_IMPERCEPTIBLE = EventFactory.createArrayBacked(HaveFlagCallback.class, callbacks -> (world, entity) -> CALLBACK_RESULT.apply(callbacks, world, entity));
    Event<HaveFlagCallback> SHOULD_BE_INDESTRUCTIBLE = EventFactory.createArrayBacked(HaveFlagCallback.class, callbacks -> (world, entity) -> CALLBACK_RESULT.apply(callbacks, world, entity));

    public ExtraFlagsComponent(Entity entity) {
        this.entity = entity;
    }

    public void sync() {
        MiaLib.FLAGS.sync(this.entity);
    }

    public boolean isImperceptible() {
        return MMath.getByteFlag(this.flags, 0);
    }

    private void setImperceptible(boolean imperceptible) {
        if (imperceptible == this.isImperceptible()) return;
        this.flags = MMath.setByteFlag(this.flags, 0, imperceptible);
        this.sync();
    }

    public boolean isIndestructible() {
        return MMath.getByteFlag(this.flags, 1);
    }

    private void setIndestructible(boolean indestructible) {
        if (indestructible == this.isIndestructible()) return;
        this.flags = MMath.setByteFlag(this.flags, 1, indestructible);
        this.sync();
    }

    public void refreshFlags() {
        this.setImperceptible(this.SHOULD_BE_IMPERCEPTIBLE.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
        this.setIndestructible(this.SHOULD_BE_INDESTRUCTIBLE.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        this.flags = tag.getByte("flags");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        tag.putByte("flags", this.flags);
    }

    static {
        CALLBACK_RESULT = (callbacks, world, entity) -> {
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
    }

    @FunctionalInterface
    interface HaveFlagCallback {
        ActionResult shouldHaveFlag(World world, Entity entity);
    }
}