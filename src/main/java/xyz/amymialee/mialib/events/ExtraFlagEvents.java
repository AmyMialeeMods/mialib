package xyz.amymialee.mialib.events;

import com.mojang.datafixers.util.Function3;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class ExtraFlagEvents {
    private static final Function3<HaveFlagCallback[], World, Entity, ActionResult> CALLBACK_RESULT = (callbacks, world, entity) -> {
        var succeeded = false;
        for (var callback : callbacks) {
            var result = callback.shouldHaveFlag(world, entity);
            if (result.isAccepted()) {
                succeeded = true;
                continue;
            }
            if (result != ActionResult.PASS) return result;
        }
        if (succeeded) return ActionResult.SUCCESS;
        return ActionResult.PASS;
    };
    public static final Event<HaveFlagCallback> SHOULD_BE_INDESTRUCTIBLE = EventFactory.createArrayBacked(HaveFlagCallback.class, callbacks -> (world, entity) -> CALLBACK_RESULT.apply(callbacks, world, entity));
    public static final Event<HaveFlagCallback> SHOULD_BE_IMMORTAL = EventFactory.createArrayBacked(HaveFlagCallback.class, callbacks -> (world, entity) -> CALLBACK_RESULT.apply(callbacks, world, entity));
    public static final Event<HaveFlagCallback> SHOULD_FLY = EventFactory.createArrayBacked(HaveFlagCallback.class, callbacks -> (world, entity) -> CALLBACK_RESULT.apply(callbacks, world, entity));

    public @FunctionalInterface interface HaveFlagCallback {
        ActionResult shouldHaveFlag(World world, Entity entity);
    }
}